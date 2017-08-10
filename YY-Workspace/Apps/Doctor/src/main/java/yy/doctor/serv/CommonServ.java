package yy.doctor.serv;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.processor.annotation.AutoIntent;
import lib.processor.annotation.Extra;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.network.Result;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 常驻服务
 *
 * @author CaiXiang
 * @since 2017/5/4
 */
@AutoIntent
public class CommonServ extends ServiceEx {

    @Extra(optional = true)
    @ReqType
    int mType;
    @Extra(optional = true)
    String mJPushRegisterId;
    @Extra(optional = true)
    String mToken;
    @Extra(optional = true)
    Submit mSubmit;
    @Extra(optional = true)
    String mMeetId;
    @Extra(optional = true)
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
                exeNetworkReq(ReqType.logout, NetFactory.logout(mToken));
            }
            break;
            case ReqType.j_push: {
                exeNetworkReq(ReqType.j_push, NetFactory.bindJPush(mJPushRegisterId));
            }
            break;
            case ReqType.video: {
                exeNetworkReq(ReqType.video, NetFactory.submitVideo()
                        .meetId(mSubmit.getString(TSubmit.meetId))
                        .moduleId(mSubmit.getString(TSubmit.moduleId))
                        .courseId(mSubmit.getString(TSubmit.courseId))
                        .detailId(mSubmit.getString(TSubmit.detailId))
                        .useTime(mSubmit.getString(TSubmit.usedtime))
                        .isFinish(mSubmit.getBoolean(TSubmit.finished))
                        .builder());
            }
            break;
            case ReqType.course: {
                exeNetworkReq(ReqType.course, NetFactory.submitPpt()
                        .meetId(mSubmit.getString(TSubmit.meetId))
                        .moduleId(mSubmit.getString(TSubmit.moduleId))
                        .courseId(mSubmit.getString(TSubmit.courseId))
                        .details(mSubmit.getString(TSubmit.times))
                        .builder());
            }
            break;
            case ReqType.meet: {
                exeNetworkReq(ReqType.meet, NetFactory.submitMeet(mMeetId, mMeetTime));
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
                    return;
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
                    return;
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
