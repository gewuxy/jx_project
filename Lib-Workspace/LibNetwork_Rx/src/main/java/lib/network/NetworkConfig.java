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
    private String mCacheDir;

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
        private long mConnectTimeout;
        private long mReadTimeout;
        private String mCacheDir;

        private Builder() {
        }

        public Builder connectTimeout(@IntRange(from = 0) long timeout) {
            mConnectTimeout = timeout;
            return this;
        }

        public Builder readTimeout(@IntRange(from = 0) long timeout) {
            mReadTimeout = timeout;
            return this;
        }

        public Builder cacheDir(@NonNull String dir) {
            mCacheDir = dir;
            return this;
        }

        public NetworkConfig build() {
            NetworkConfig config = new NetworkConfig();

            config.mConnectTimeout = mConnectTimeout;
            config.mReadTimeout = mReadTimeout;
            config.mCacheDir= mCacheDir;

            return config;
        }
    }
}
