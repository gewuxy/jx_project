package lib.ys.ui.frag;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.webkit.WebView;
import android.widget.ProgressBar;

import lib.ys.R;
import lib.ys.ui.interfaces.web.IWebViewHost;
import lib.ys.ui.interfaces.impl.WebOption;
import lib.ys.ui.interfaces.impl.WebViewSetter;

/**
 * @author yuansui
 */
abstract public class WebViewFragEx extends FragEx implements IWebViewHost {


    private WebView mWebView;
    private ProgressBar mProgressBar;

    private WebViewSetter mSetter;

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
        mSetter = new WebViewSetter(this);
        mSetter.set(mWebView, mProgressBar);

        onLoadStart();
    }

    /**
     * 初始化完后第一次加载url, 强制执行
     */
    abstract protected void onLoadStart();

    @CallSuper
    @Override
    protected void onResultData(int requestCode, int resultCode, Intent data) {
        mSetter.onResultData(requestCode, resultCode, data);
    }

    /**
     * 抓取h5链接中的title
     *
     * @param mH5Title
     */
    public void setH5Title(String mH5Title) {
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

    protected WebView getWebView() {
        return mWebView;
    }

    @Override
    public WebOption getOption() {
        return WebOption.newBuilder().build();
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
