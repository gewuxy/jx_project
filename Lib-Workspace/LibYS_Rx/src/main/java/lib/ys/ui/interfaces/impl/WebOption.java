package lib.ys.ui.interfaces.impl;

import android.graphics.drawable.Drawable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import lib.ys.ConstantsEx;

public class WebOption {
    private int mScrollBarStyle;
    private int mCacheMode;

    private Boolean mIsDomStorageEnabled;
    private Boolean mIsBuiltInZoomControlsEnabled;
    private Boolean mIsScaleEnabled;
    private Boolean mIsJsEnabled;

    private WebViewClient mClient;
    private Drawable mProgressBarDrawable;


    private WebOption() {
        mScrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY;
        mCacheMode = WebSettings.LOAD_DEFAULT;

        mIsDomStorageEnabled = true;
        mIsBuiltInZoomControlsEnabled = true;
        mIsScaleEnabled = true;
        mIsJsEnabled = true;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getScrollBarStyle() {
        return mScrollBarStyle;
    }

    public int getCacheMode() {
        return mCacheMode;
    }

    public boolean isDomStorageEnabled() {
        return mIsDomStorageEnabled;
    }

    public boolean isBuiltInZoomControlsEnabled() {
        return mIsBuiltInZoomControlsEnabled;
    }

    public boolean isScaleEnabled() {
        return mIsScaleEnabled;
    }

    public boolean isJsEnabled() {
        return mIsJsEnabled;
    }

    public Drawable getProgressBarDrawable() {
        return mProgressBarDrawable;
    }

    public WebViewClient getClient() {
        if (mClient == null) {
            mClient = new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                    view.loadUrl(url);
                    return true;
                }
            };
        }
        return mClient;
    }


    public static class Builder {
        private int mScrollBarStyle = ConstantsEx.KInvalidValue;
        private int mCacheMode = ConstantsEx.KInvalidValue;

        private Boolean mIsDomStorageEnabled;
        private Boolean mIsBuiltInZoomControlsEnabled;
        private Boolean mIsScaleEnabled;
        private Boolean mIsJsEnabled;

        private WebViewClient mClient;
        private Drawable mProgressBarDrawable;


        private Builder() {
        }

        /**
         * @param style {@link WebView#SCROLLBARS_INSIDE_OVERLAY}
         *              {@link WebView#SCROLLBARS_INSIDE_INSET}
         *              {@link WebView#SCROLLBARS_OUTSIDE_OVERLAY}
         *              {@link WebView#SCROLLBARS_OUTSIDE_INSET}
         * @return
         */
        public Builder scrollBarStyle(int style) {
            mScrollBarStyle = style;
            return this;
        }

        /**
         * 是否使用DOM storage API
         *
         * @return
         */
        public Builder enableDomStorage(boolean enabled) {
            mIsDomStorageEnabled = enabled;
            return this;
        }

        /**
         * 使用放大缩小按钮, 应该和{@link #enableScale(boolean)} ()}保持一致
         *
         * @return
         */
        public Builder enableBuiltInZoomControls(boolean enabled) {
            mIsBuiltInZoomControlsEnabled = enabled;
            return this;
        }

        /**
         * 允许缩放
         *
         * @return
         * @see #enableBuiltInZoomControls(boolean)
         */
        public Builder enableScale(boolean enabled) {
            mIsScaleEnabled = enabled;
            return this;
        }

        public Builder client(WebViewClient client) {
            mClient = client;
            return this;
        }

        public Builder progressBarDrawable(Drawable d) {
            mProgressBarDrawable = d;
            return this;
        }

        /**
         * 是否允许运行JS
         *
         * @return
         */
        public Builder enableJs(boolean enabled) {
            mIsJsEnabled = enabled;
            return this;
        }

        /**
         * 获取缓存模式
         * <pre>
         * 默认(有网络加载网络, 无网络加载缓存):{@link WebSettings#LOAD_DEFAULT}
         * 无缓存:{@link WebSettings#LOAD_NO_CACHE}
         * 只加载缓存:{@link WebSettings#LOAD_CACHE_ONLY}
         * 加载缓存(无论是否过期), 如果没有则联网:{@link WebSettings#LOAD_CACHE_ELSE_NETWORK}
         *
         * @return 当前模式
         */
        public Builder getCacheMode(int mode) {
            mCacheMode = mode;
            return this;
        }

        public WebOption build() {
            WebOption op = new WebOption();

            if (invalid(mScrollBarStyle)) {
                op.mScrollBarStyle = mScrollBarStyle;
            }

            if (invalid(mCacheMode)) {
                op.mCacheMode = mCacheMode;
            }

            if (invalid(mIsDomStorageEnabled)) {
                op.mIsDomStorageEnabled = mIsDomStorageEnabled;
            }

            if (invalid(mIsBuiltInZoomControlsEnabled)) {
                op.mIsBuiltInZoomControlsEnabled = mIsBuiltInZoomControlsEnabled;
            }

            if (invalid(mIsScaleEnabled)) {
                op.mIsScaleEnabled = mIsScaleEnabled;
            }

            if (invalid(mIsJsEnabled)) {
                op.mIsJsEnabled = mIsJsEnabled;
            }

            if (mClient != null) {
                op.mClient = mClient;
            }

            if (mProgressBarDrawable != null) {
                op.mProgressBarDrawable = mProgressBarDrawable;
            }

            return op;
        }

        private boolean invalid(int val) {
            return val != ConstantsEx.KInvalidValue;
        }

        private boolean invalid(Boolean val) {
            return val != null;
        }
    }
}