package lib.ys.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import lib.ys.R;
import lib.ys.ui.interfaces.IWebViewHost;
import lib.ys.ui.interfaces.impl.WebViewOptImpl;


abstract public class WebViewActivityEx extends ActivityEx implements IWebViewHost {

    private WebView mWebView;
    private ProgressBar mProgressBar;

    private WebViewOptImpl mImpl;

    @Override
    public int getContentViewId() {
        return R.layout.activity_web_view_ex;
    }

    @CallSuper
    @Override
    public void findViews() {
        mWebView = findView(R.id.web_view_ex_wv);
        mProgressBar = findView(R.id.web_view_ex_progress_bar);
    }

    @CallSuper
    @Override
    public void setViews() {
        mImpl = new WebViewOptImpl(this);
        mImpl.setViews(mProgressBar, mWebView);

        onLoadStart();
    }

    /**
     * 初始化完后第一次加载url, 强制执行
     */
    abstract protected void onLoadStart();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImpl.onResultData(requestCode, resultCode, data);
    }

    /**
     * 抓取h5链接中的title
     *
     * @param mH5Title
     */
    public void setH5Title(String mH5Title) {
    }

    @Override
    public WebViewClient getWebViewClient() {
        return new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);
                return true;
            }
        };
    }

    @Override
    public boolean enableJs() {
        return true;
    }

    @Override
    public boolean enableScale() {
        return true;
    }

    @Override
    public boolean enableBuiltInZoomControls() {
        return true;
    }

    @Override
    public boolean enableDomStorage() {
        return true;
    }

    @Override
    public int getScrollBarStyle() {
        return WebView.SCROLLBARS_INSIDE_OVERLAY;
    }

    @Override
    public int getCacheMode() {
        return WebSettings.LOAD_DEFAULT;
    }

    @Override
    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    @Override
    public boolean canGoForward() {
        return mWebView.canGoForward();
    }

    @Override
    public void goBack() {
        mWebView.goBack();
    }

    @Override
    public void goForward() {
        mWebView.goForward();
    }

    @Override
    public Drawable getProgressBarDrawable() {
        return null;
    }

    protected WebView getWebView() {
        return mWebView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }
}
