package lib.network.provider.ok.callback;

import java.io.IOException;

import lib.network.NetworkLog;
import lib.network.model.NetworkErrorBuilder;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.NativeListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 以bytes的形式返回
 *
 * @auther yuansui
 * @since 2017/6/11
 */
public class DownloadCallback extends OkCallback {

    public DownloadCallback(OnNetworkListener l) {
        super(l);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        NetworkResp resp = new NetworkResp();
        resp.setBytes(response.body().bytes());

        Integer id = (Integer) call.request().tag();
        try {
            Object result = getListener().onNetworkResponse(id, resp);
            if (result != null) {
                NativeListener.inst().onSuccess(id, result, getListener());
            }
        } catch (Exception e) {
            NetworkLog.e("onResponse", e);
            NativeListener.inst().onError(id,
                    NetworkErrorBuilder.create().code(id).exception(e).message(e.getMessage()).build(),
                    getListener());
        }
    }
}
