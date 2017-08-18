package lib.network;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import inject.annotation.builder.Builder;
import lib.network.model.interfaces.IConfigsMaker;
import lib.network.model.param.CommonPair;

/**
 * @auther yuansui
 * @since 2017/5/16
 */
@Builder
public class NetworkConfig {

    // 链接时间
    @IntRange(from = 0)
    long mConnectTimeout;

    // 读取时间
    @IntRange(from = 0)
    long mReadTimeout;

    // 写入时间
    @IntRange(from = 0)
    long mWriteTimeout;

    // 缓存地址
    @NonNull
    String mCacheDir;
    // 网络超时
    String mTimeoutToast;
    // 网络未连接
    String mDisconnectToast;
    // 共用参数
    IConfigsMaker mParamsMaker;
    // 共用headers
    IConfigsMaker mHeadersMaker;

    public NetworkConfig() {
        mConnectTimeout = 15;
        mReadTimeout = 15;
        mWriteTimeout = 15;

        mTimeoutToast = "网络超时，请检查网络设置";
        mDisconnectToast = "当前网络不可用，请检查网络设置";
    }

    public String getTimeoutToast() {
        return mTimeoutToast;
    }

    public String getDisconnectToast() {
        return mDisconnectToast;
    }

    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public String getCacheDir() {
        return mCacheDir;
    }

    @Nullable
    public List<CommonPair> getCommonParams() {
        if (mParamsMaker != null) {
            return mParamsMaker.make();
        } else {
            return null;
        }
    }

    @Nullable
    public List<CommonPair> getCommonHeaders() {
        if (mHeadersMaker != null) {
            return mHeadersMaker.make();
        } else {
            return null;
        }
    }

    public static NetworkConfigBuilder newBuilder() {
        return NetworkConfigBuilder.create();
    }
}
