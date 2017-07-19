package lib.network;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * @auther yuansui
 * @since 2017/5/16
 */

public class NetworkConfig {
    private long mConnectTimeout;
    private long mReadTimeout;
    private long mWriteTimeout;

    private String mCacheDir;

    // 网络超时
    private String mTimeoutToast;
    // 网络未连接
    private String mDisconnectToast;

    private NetworkConfig() {
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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long mConnectTimeout;
        private Long mReadTimeout;
        private Long mWriteTimeout;
        private String mCacheDir;

        private String mTimeoutToast;
        private String mDisconnectToast;

        private Builder() {
        }

        /**
         * 链接超时
         *
         * @param timeout 单位: 秒
         * @return
         */
        public Builder connectTimeout(@IntRange(from = 0) long timeout) {
            mConnectTimeout = timeout;
            return this;
        }

        /**
         * 读取超时
         *
         * @param timeout 单位: 秒
         * @return
         */
        public Builder readTimeout(@IntRange(from = 0) long timeout) {
            mReadTimeout = timeout;
            return this;
        }

        /**
         * 写入超时
         *
         * @param timeout 单位: 秒
         * @return
         */
        public Builder writeTimeout(@IntRange(from = 0) long timeout) {
            mWriteTimeout = timeout;
            return this;
        }

        public Builder cacheDir(@NonNull String dir) {
            mCacheDir = dir;
            return this;
        }

        public Builder timeoutToast(@NonNull String toast) {
            mTimeoutToast = toast;
            return this;
        }

        public Builder disconnectToast(@NonNull String toast) {
            mDisconnectToast = toast;
            return this;
        }

        public NetworkConfig build() {
            NetworkConfig config = new NetworkConfig();

            if (mConnectTimeout != null) {
                config.mConnectTimeout = mConnectTimeout;
            }

            if (mReadTimeout != null) {
                config.mReadTimeout = mReadTimeout;
            }

            if (mWriteTimeout != null) {
                config.mWriteTimeout = mWriteTimeout;
            }

            config.mCacheDir = mCacheDir;

            if (mTimeoutToast != null) {
                config.mTimeoutToast = mTimeoutToast;
            }

            if (mDisconnectToast != null) {
                config.mDisconnectToast = mDisconnectToast;
            }

            return config;
        }
    }
}
