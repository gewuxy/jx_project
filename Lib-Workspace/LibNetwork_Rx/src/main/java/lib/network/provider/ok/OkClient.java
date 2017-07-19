package lib.network.provider.ok;

import java.util.concurrent.TimeUnit;

import lib.network.Network;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @auther yuansui
 * @since 2017/6/10
 */

public class OkClient {
    private OkHttpClient mClient;

    private static OkClient mInst;

    private OkClient() {
        mClient = new OkHttpClient.Builder()
                .connectTimeout(Network.getConfig().getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(Network.getConfig().getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(Network.getConfig().getWriteTimeout(), TimeUnit.SECONDS)
                .build();
    }

    public static synchronized OkClient inst() {
        if (mInst == null) {
            mInst = new OkClient();
        }
        return mInst;
    }

    public Call newCall(Request r) {
        return mClient.newCall(r);
    }

    public WebSocket newWebSocket(Request request, WebSocketListener listener) {
        return mClient.newWebSocket(request, listener);
    }

    public OkHttpClient addInterceptor(Interceptor interceptor) {
        return mClient.newBuilder()
                .addNetworkInterceptor(interceptor)
                .build();
    }
}
