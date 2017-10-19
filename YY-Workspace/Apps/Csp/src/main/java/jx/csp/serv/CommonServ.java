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
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpUser;
import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/16
 */
@Route
public class CommonServ extends ServiceEx{

    @Arg(opt = true)
    @ReqType
    int mType;
    @Arg(opt = true)
    String mToken;
    @Arg(opt = true)
    String mJPushRegisterId;

    @IntDef({
            ReqType.logout,
            ReqType.j_push,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReqType {
        int logout = 1;
        int j_push = 2;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (mType) {
            case ReqType.logout: {
                exeNetworkReq(mType, UserAPI.logout().build());
            }
            break;
            case ReqType.j_push: {

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
        switch (id) {
            case ReqType.logout: {
                if (r.isSucceed()) {
                    //清空个人信息，把极光绑定改为false 登录后需要重新绑定
                    SpUser.inst().clear();
                    SpJPush.inst().jPushIsRegister(false);
                    Profile.inst().clear();
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
        }
    }
}
