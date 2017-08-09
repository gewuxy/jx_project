package lib.network.provider.ok.callback;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import lib.network.Network;
import lib.network.model.NetworkErrorBuilder;
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
        // FIXME: err code暂时获取不到, 需要再new call的时候加入interceptor, 通过response获取
        Integer id = (Integer) call.request().tag();

        NetworkErrorBuilder builder = NetworkErrorBuilder.create()
                .code(id)
                .exception(e);

        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException || e instanceof UnknownServiceException) {
            // 目前超时/未知host/未知服务错误, 都是用超时的处理
            builder.message(Network.getConfig().getTimeoutToast());
        } else if (e instanceof SocketException) {
            // socket链接中断, 暂时认为是取消
            builder.message("socket链接中断");
        } else {
            if (e.getMessage().equals("Canceled")) {
                builder.message("任务取消");
            } else {
                builder.message(e.getMessage());
            }
        }

        NativeListener.inst().onError(id, builder.build(), mListener);
    }

    public OnNetworkListener getListener() {
        return mListener;
    }
}