package yy.doctor.serv;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.network.Result;
import yy.doctor.Extra;
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
public class CommonServ extends ServiceEx {

    private static final int KIdLogout = 1;
    private static final int KIdJPush = 2;
    private static final int KIdVideo = 3;
    private static final int KIdPPT = 4;
    private static final int KIdMeet = 5;

    private String mJPushRegisterId;
    private String mToken;

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
        int type = intent.getIntExtra(Extra.KType, 0);
        switch (type) {
            case ReqType.logout: {
                mToken = intent.getStringExtra(Extra.KData);
                exeNetworkReq(KIdLogout, NetFactory.logout(mToken));
            }
            break;

            case ReqType.j_push: {
                mJPushRegisterId = intent.getStringExtra(Extra.KData);
                exeNetworkReq(KIdJPush, NetFactory.bindJPush(mJPushRegisterId));
            }
            break;

            case ReqType.video: {
                Submit submit = (Submit) intent.getSerializableExtra(Extra.KData);
                exeNetworkReq(KIdVideo, NetFactory.submitVideo()
                        .meetId(submit.getString(TSubmit.meetId))
                        .moduleId(submit.getString(TSubmit.moduleId))
                        .courseId(submit.getString(TSubmit.courseId))
                        .detailId(submit.getString(TSubmit.detailId))
                        .useTime(submit.getString(TSubmit.usedtime))
                        .isFinish(submit.getBoolean(TSubmit.finished))
                        .builder());
            }
            break;

            case ReqType.course: {
                Submit submit = (Submit) intent.getSerializableExtra(Extra.KData);
                exeNetworkReq(KIdPPT, NetFactory.submitPpt()
                        .meetId(submit.getString(TSubmit.meetId))
                        .moduleId(submit.getString(TSubmit.moduleId))
                        .courseId(submit.getString(TSubmit.courseId))
                        .details(submit.getString(TSubmit.times))
                        .builder());
            }
            break;

            case ReqType.meet: {
                exeNetworkReq(KIdMeet, NetFactory.submitMeet(intent.getStringExtra(Extra.KMeetId),
                        intent.getLongExtra(Extra.KData, 0)));
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
            case KIdLogout: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "退出账号成功");
                } else {
                    retryNetworkRequest(id);
                    YSLog.d(TAG, "退出账号失败");
                    return;
                }
            }
            break;

            case KIdJPush: {
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
            case KIdPPT:
            case KIdMeet:
            case KIdVideo: {
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
