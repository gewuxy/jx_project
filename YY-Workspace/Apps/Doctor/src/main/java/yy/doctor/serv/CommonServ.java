package yy.doctor.serv;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.network.Result;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.network.NetworkAPISetter.UserAPI;

/**
 * 常驻服务
 *
 * @author CaiXiang
 * @since 2017/5/4
 */
@Route
public class CommonServ extends ServiceEx {

    @Arg(opt = true)
    @ReqType
    int mType;
    @Arg(opt = true)
    String mJPushRegisterId;
    @Arg(opt = true)
    String mToken;
    @Arg(opt = true)
    Submit mSubmit;
    @Arg(opt = true)
    String mMeetId;
    @Arg(opt = true)
    long mMeetTime;

    @IntDef({
            ReqType.logout,
            ReqType.j_push,
            ReqType.video,
            ReqType.course,
            ReqType.meet,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReqType {
        int logout = 1;
        int j_push = 2;
        int video = 3;
        int course = 4;
        int meet = 5;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (mType) {
            case ReqType.logout: {
                exeNetworkReq(mType, UserAPI.logout(mToken).build());
            }
            break;
            case ReqType.j_push: {
                exeNetworkReq(mType, UserAPI.bindJPush(mJPushRegisterId).build());
            }
            break;
            case ReqType.video: {
                exeNetworkReq(mType, MeetAPI.submitVideo()
                        .meetId(mSubmit.getString(TSubmit.meetId))
                        .moduleId(mSubmit.getString(TSubmit.moduleId))
                        .courseId(mSubmit.getString(TSubmit.courseId))
                        .detailId(mSubmit.getString(TSubmit.detailId))
                        .usedTime(mSubmit.getString(TSubmit.usedtime))
                        .finished(mSubmit.getBoolean(TSubmit.finished))
                        .build());
            }
            break;
            case ReqType.course: {
                NetworkReq r = MeetAPI.submitCourse()
                        .meetId(mSubmit.getString(TSubmit.meetId))
                        .moduleId(mSubmit.getString(TSubmit.moduleId))
                        .courseId(mSubmit.getString(TSubmit.courseId))
                        .details(mSubmit.getString(TSubmit.times))
                        .build();
                exeNetworkReq(mType, r);
            }
            break;
            case ReqType.meet: {
                exeNetworkReq(mType, MeetAPI.submitMeet(mMeetId, mMeetTime).build());
            }
            break;
        }

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws JSONException {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;

        //通过id判断 执行的网络请求
        switch (id) {
            case ReqType.logout: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "退出账号成功");
                } else {
                    retryNetworkRequest(id);
                    YSLog.d(TAG, "退出账号失败");
                }
            }
            break;

            case ReqType.j_push: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "极光推送绑定成功");
                    SpJPush.inst().jPushRegisterId(mJPushRegisterId);
                    SpJPush.inst().jPushIsRegister(true);
                } else {
                    YSLog.d(TAG, "极光推送绑定失败");
                    retryNetworkRequest(id);
                    SpJPush.inst().jPushIsRegister(false);
                }
            }
            break;

            // 记录时间的都同一操作
            case ReqType.video:
            case ReqType.course:
            case ReqType.meet: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "onNetworkSuccess:记录成功");
                } else {
                    retryNetworkRequest(id);
                    YSLog.d(TAG, "onNetworkSuccess:记录失败");
                }
            }
            break;
        }
    }

}
