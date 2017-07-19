package lib.network.provider.ok.callback;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import lib.network.model.err.NetError;
import lib.network.model.err.TimeoutError;
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
        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException || e instanceof UnknownServiceException) {
            // 目前超时/未知host/未知服务错误, 都是用超时的处理
            NativeListener.inst().onError(id, new TimeoutError(id), mListener);
        } else {
            // FIXME: err code暂时获取不到, 需要再new call的时候加入interceptor, 通过response获取
            NativeListener.inst().onError(id, new NetError(id, e.getMessage()), mListener);
        }
    }

    public OnNetworkListener getListener() {
        return mListener;
    }
}