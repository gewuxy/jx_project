package jx.csp.serv;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.model.Profile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpUser;
import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;

/**
 * @auther Huoxuyu
 * @since 2017/10/16
 */
@Route
public class CommonServ extends ServiceEx {

    @Arg
    @ReqType
    int mType;

    @Arg(opt = true)
    String mToken;

    @Arg(opt = true)
    String mJPushRegisterId;

    @Arg(opt = true)
    String mCourseId;

    @Arg(opt = true)
    int mPageNum;

    @Arg(opt = true)
    int mOverType;
    @Arg(opt = true)
    String mUrl;

    @Arg(opt = true)
    String mFileName;

    @Arg(opt = true)
    int mNewVersion;

    @Arg(opt = true)
    String mTitle;

    @IntDef({
            ReqType.logout,
            ReqType.j_push,
            ReqType.exit_record,
            ReqType.share_delete_meet,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReqType {
        int logout = 1;
        int j_push = 2;
        int exit_record = 3;
        int share_delete_meet = 4;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (mType) {
            case ReqType.logout: {
                exeNetworkReq(mType, UserAPI.logout().build());
            }
            break;
            case ReqType.j_push: {
                exeNetworkReq(mType, UserAPI.bindJPush(mJPushRegisterId).build());
            }
            break;
            case ReqType.exit_record: {
                exeNetworkReq(mType, MeetingAPI.exitRecord(mCourseId, mPageNum, mOverType).build());
            }
            break;
            case ReqType.share_delete_meet: {
                exeNetworkReq(mType, MeetingAPI.delete(mCourseId).build());
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws JSONException {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        switch (id) {
            case ReqType.logout: {
                if (r.isSucceed()) {
                    //清空个人信息，把极光绑定改为false 登录后需要重新绑定
                    SpUser.inst().clear();
                    SpJPush.inst().jPushIsRegister(false);
                    Profile.inst().clear();

                    Notifier.inst().notify(NotifyType.logout);
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
            case ReqType.exit_record: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "退出录音成功");
                } else {
                    YSLog.d(TAG, "退出录音失败");
                }
            }
            break;
            case ReqType.share_delete_meet: {
                if (r.isSucceed()) {
                    YSLog.d(TAG, "发送删除通知");
                    Notifier.inst().notify(NotifyType.delete_meeting, mCourseId);
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
        }
    }
}
