package yy.doctor.serv;

import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;

import lib.network.model.NetworkResp;
import lib.ys.service.ServiceEx;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpUser;
import yy.doctor.sp.SpUser.SpUserKey;

/**
 * 常驻服务
 *
 * @author CaiXiang
 * @since 2017/5/4
 */
public class CommonServ extends ServiceEx {

    private static final int KReqIdLogout = 1;
    private static final int KReqIdJPushRegisterId = 2;

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
                    SpUser.inst().clear();
                } else {
                    retryNetworkRequest(id);
                    return;
                }
                Profile.inst().clear();
            }
            break;
            case KReqIdJPushRegisterId: {
                if (r.isSucceed()) {
                    SpUser.inst().save(SpUserKey.KJPushRegisterId, mJPushRegisterId);
                } else {
                    retryNetworkRequest(id);
                    return;
                }
            }
            break;
        }
    }

}
