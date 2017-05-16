package lib.network;

import android.support.annotation.IntRange;

/**
 * @auther yuansui
 * @since 2017/5/16
 */

public class NetworkConfig {
    private long mConnectTimeout;
    private long mReadTimeout;

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private long mConnectTimeout;
        private long mReadTimeout;

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

        public NetworkConfig build() {
            NetworkConfig config = new NetworkConfig();

            config.mConnectTimeout = mConnectTimeout;
            config.mReadTimeout = mReadTimeout;

            return config;
        }
    }
}
