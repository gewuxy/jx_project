package lib.network;


import android.content.Context;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.BaseProvider;
import lib.network.provider.ok.OkProvider;
import okhttp3.OkHttpClient;

/**
 * 网络任务执行者
 *
 * @author yuansui
 * @since 2016/4/11
 */
public class Network {
    private OnNetworkListener mListener;
    private BaseProvider mProvider;

    private static Context mContext;

    public Network(Object tag, OnNetworkListener listener) {
        mListener = listener;
        mProvider = new OkProvider(tag);
    }

    public void execute(int id, NetworkReq task) {
        execute(id, task, mListener);
    }

    public void execute(int id, NetworkReq request, OnNetworkListener listener) {
        if (listener == null) {
            listener = mListener;
        }
        mProvider.load(request, id, listener);
    }

    public void cancel(int id) {
        mProvider.cancel(id);
    }

    public void cancelAll() {
        mProvider.cancelAll();
    }

    public void destroy() {
        cancelAll();
    }

    public static void init(Context context, NetworkConfig config) {
        mContext = context;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public static Context getContext() {
        return mContext;
    }
}
