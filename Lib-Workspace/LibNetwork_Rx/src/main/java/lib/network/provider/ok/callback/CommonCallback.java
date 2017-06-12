package lib.network.provider.ok.callback;

import java.io.IOException;

import lib.network.LogNetwork;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.NativeListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @auther yuansui
 * @since 2017/6/11
 */

public class CommonCallback extends OkCallback {

    public CommonCallback(OnNetworkListener l) {
        super(l);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String str = response.body().string();
        NetworkResp resp = new NetworkResp();
        resp.setText(str);

        Integer id = (Integer) call.request().tag();
        try {
            Object result = getListener().onNetworkResponse(id, resp);
            if (result != null) {
                NativeListener.inst().onSuccess(id, result, getListener());
            }
        } catch (Exception e) {
            LogNetwork.e("onResponse", e);
        }
    }
}
