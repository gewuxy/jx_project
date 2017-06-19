package yy.doctor.serv;

import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;

import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpUser;

/**
 * 常驻服务
 *
 * @author CaiXiang
 * @since 2017/5/4
 */
public class CommonServ extends ServiceEx {

    private static final int KReqIdLogout = 1;
    private static final int KReqIdJPushRegisterId = 2;
    private static final int KReqIdVideo = 2;

    private String mJPushRegisterId;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String type = intent.getStringExtra(Extra.KType);
        if (type.equals(Extra.KLogout)) {
            exeNetworkReq(KReqIdLogout, NetFactory.logout());
        } else if (type.equals(Extra.KJPushRegisterId)) {
            mJPushRegisterId = intent.getStringExtra(Extra.KData);
            exeNetworkReq(KReqIdJPushRegisterId, NetFactory.bindJPush(mJPushRegisterId));
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
            case KReqIdLogout: {
                if (r.isSucceed()) {
                    //清空个人信息，把极光绑定改为false 登录后需要重新绑定
                    SpUser.inst().clear();
                    SpJPush.inst().jPushIsRegister(false);
                } else {
                    retryNetworkRequest(id);
                    return;
                }
                Profile.inst().clearProfile();
            }
            break;
            case KReqIdJPushRegisterId: {
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
        }
    }

}
