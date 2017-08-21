package lib.ys.ui.interfaces.impl;

import android.graphics.drawable.Drawable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import inject.annotation.builder.Builder;
import inject.annotation.builder.Ignore;
import lib.ys.ui.interfaces.web.OnWebError;
import lib.ys.ui.interfaces.web.OnWebLoading;
import lib.ys.ui.interfaces.web.OnWebSuccess;

@Builder
public class WebOption {
    /**
     * scroll bar样式
     * <pre>
     * {@link WebView#SCROLLBARS_INSIDE_OVERLAY}
     * {@link WebView#SCROLLBARS_INSIDE_INSET}
     * {@link WebView#SCROLLBARS_OUTSIDE_OVERLAY}
     * {@link WebView#SCROLLBARS_OUTSIDE_INSET}
     */
    int mScrollBarStyle;

    /**
     * 缓存模式
     * <pre>
     * 默认(有网络加载网络, 无网络加载缓存):{@link WebSettings#LOAD_DEFAULT}
     * 无缓存:{@link WebSettings#LOAD_NO_CACHE}
     * 只加载缓存:{@link WebSettings#LOAD_CACHE_ONLY}
     * 加载缓存(无论是否过期), 如果没有则联网:{@link WebSettings#LOAD_CACHE_ELSE_NETWORK}
     */
    int mCacheMode;

    /**
     * 是否使用DOM storage API
     */
    boolean mEnableDomStorage;

    /**
     * 使用放大缩小按钮, 应该和{@link #mEnableDomStorage} ()}保持一致
     */
    boolean mEnableBuiltInZoomControls;

    /**
     * 允许缩放
     */
    boolean mEnableScale;
    boolean mEnableJs;

    Drawable mProgressBarDrawable;

    OnWebLoading mOnLoading;
    OnWebError mOnError;
    OnWebSuccess mOnSuccess;

    @Ignore
    WebViewClient mClient;

    protected WebOption() {
        mScrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY;
        mCacheMode = WebSettings.LOAD_DEFAULT;

        mEnableDomStorage = true;
        mEnableBuiltInZoomControls = true;
        mEnableScale = true;
        mEnableJs = true;

        mClient = new WebViewClient() {

            private boolean mSuccess = true;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mSuccess = true;
                if (mOnLoading != null) {
                    return mOnLoading.loading(view, url);
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mSuccess = false;
                if (mOnError != null) {
                    mOnError.onError(view, errorCode, description, failingUrl);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mSuccess) {
                    if (mOnSuccess != null) {
                        mOnSuccess.onSuccess(view, url);
                    }
                }
            }
        };
    }

    public static WebOptionBuilder newBuilder() {
        return WebOptionBuilder.create();
    }

    public int getScrollBarStyle() {
        return mScrollBarStyle;
    }

    public int getCacheMode() {
        return mCacheMode;
    }

    public boolean isDomStorageEnabled() {
        return mEnableDomStorage;
    }

    public boolean isBuiltInZoomControlsEnabled() {
        return mEnableBuiltInZoomControls;
    }

    public boolean isScaleEnabled() {
        return mEnableScale;
    }

    public boolean isJsEnabled() {
        return mEnableJs;
    }

    public Drawable getProgressBarDrawable() {
        return mProgressBarDrawable;
    }

    public WebViewClient getClient() {
        return mClient;
    }
}