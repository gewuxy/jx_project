package yy.doctor.serv;

import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;

import lib.network.model.NetworkResp;
import lib.ys.service.ServiceEx;
import lib.yy.network.Result;
import yy.doctor.model.Logout;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpUser;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class LogoutServ extends ServiceEx {

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        exeNetworkReq(NetFactory.logout());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws JSONException {
        return JsonParser.ev(r.getText(), Logout.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Logout> r = (Result<Logout>) result;

        Profile.inst().clear();
        if (r.isSucceed()) {
            SpUser.inst().clear();
        } else {
            retryNetworkRequest(id);
        }
    }

}
