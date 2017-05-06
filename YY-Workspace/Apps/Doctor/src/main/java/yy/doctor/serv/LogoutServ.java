package yy.doctor.serv;

import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;

import lib.network.model.NetworkResponse;
import lib.network.model.NetworkRetry;
import lib.ys.service.ServiceEx;
import lib.yy.network.Response;
import yy.doctor.model.Logout;
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

        exeNetworkRequest(0, NetFactory.logout().retry(new NetworkRetry(5, 1)));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws JSONException {

        return JsonParser.ev(nr.getText(), Logout.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        Response<Logout> r = (Response<Logout>) result;

        if (r.isSucceed()) {
            SpUser.inst().clear();
        } else {
            retryNetworkRequest(id);
        }
    }

}
