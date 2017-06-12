package lib.network.provider.ok.callback;

import java.io.IOException;

import lib.network.model.err.NetError;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.NativeListener;
import okhttp3.Call;
import okhttp3.Callback;


/**
 * @auther yuansui
 * @since 2017/6/10
 */

abstract public class OkCallback implements Callback {

    private OnNetworkListener mListener;

    public OkCallback(OnNetworkListener l) {
        mListener = l;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Integer id = (Integer) call.request().tag();
        NativeListener.inst().onError(id, new NetError(id, e.getMessage()), mListener);
    }

    public OnNetworkListener getListener() {
        return mListener;
    }
}
