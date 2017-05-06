package lib.ys.frag;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.File;

import lib.ys.AppEx;
import lib.ys.R;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;

/**
 * @author yuansui
 */
abstract public class WebViewFragEx extends FragEx {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    protected ValueCallback<Uri> mUploadMessage;
    protected ValueCallback<Uri[]> mUploadMsgs;

    @Override
    public int getContentViewId() {
        return R.layout.activity_web_view_ex;
    }

    @Override
    public void findViews() {
        mWebView = findView(R.id.web_view_ex_wv);
        mProgressBar = findView(R.id.web_view_ex_progress_bar);
    }

    @Override
    public void setViews() {
        Drawable pd = getProgressBarDrawable();
        if (pd != null) {
            /*
             * 这里必须使用ClipDrawable, 不然的话效果会变成整个drawable平铺, 看不出来进度了
			 */
            ClipDrawable d = new ClipDrawable(pd, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            mProgressBar.setProgressDrawable(d);
        }

		/*
         * 设置web view的属性
		 */
        WebSettings settings = mWebView.getSettings();

        settings.setCacheMode(getCacheMode());
        settings.setJavaScriptEnabled(enableJs());
        settings.setUseWideViewPort(enableScale());
        settings.setBuiltInZoomControls(enableBuiltInZoomControls());
        settings.setDomStorageEnabled(enableDomStorage());
        settings.setAllowFileAccess(true);

        mWebView.setScrollBarStyle(getScrollBarStyle());

        mWebView.setWebViewClient(getWebViewClient());

        // 自己管理进度条的展现形式, 暂时不对外开放
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    ViewUtil.goneView(mProgressBar);
                } else {
                    ViewUtil.showView(mProgressBar);
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setH5Title(title);
            }

            // 4.0以上（除4.4 4.4暂时没有系统发放）
            @JavascriptInterface
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                updateFile(uploadMsg);
            }

            // 3.0-4.0
            @JavascriptInterface
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                updateFile(uploadMsg);
            }

            // 3.0以下
            @JavascriptInterface
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                updateFile(uploadMsg);
            }

            // 5.0以上使用（目前发现不写也可以）
//			@Override
//			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//				updateFileForLollipop(filePathCallback);
//				return true;
//			}
        });

        onLoadStart();
    }

    /**
     * 初始化完后第一次加载url, 强制执行
     */
    abstract protected void onLoadStart();

    /**
     * webview上传文件
     *
     * @param uploadMsg
     */
    @JavascriptInterface
    protected void updateFile(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择要使用的应用"), 1);
    }

    /**
     * webview上传文件5.0以上
     *
     * @param uploadMsg
     */
    @JavascriptInterface
    protected void updateFileForLollipop(ValueCallback<Uri[]> uploadMsg) {
        mUploadMsgs = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择要使用的应用"), 2);
    }

    @Override
    protected void onResultData(int requestCode, int resultCode, Intent data) {
        // 不能写此判断，不能调到onReceiveValue()，导致再次点击添加图片无效
        // if (resultCode != RESULT_OK) {1
        // return;
        // }
        if (requestCode == 1) {
            if (null != mUploadMessage) {
//				Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                Uri result = data == null || resultCode != RESULT_OK ? null : Uri.fromFile(new File(decodePicUri(data.getData())));
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else if (requestCode == 2) {
            // 5.0以上使用（目前发现不写也可以）
            if (mUploadMsgs != null) {
                Uri[] results = null;
                String dataString = data.getDataString();
                if (!TextUtil.isEmpty(dataString)) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
                mUploadMsgs.onReceiveValue(results);
                mUploadMsgs = null;
            }
        }
    }

    /**
     * @param uri
     * @return
     */
    public static String decodePicUri(Uri uri) {
        String picPath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = AppEx.getExContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picPath = cursor.getString(columnIndex);
            cursor.close();
        } else {
            // 使用es文件浏览器打开
            if (uri != null) {
                System.gc();// 立即回收
                picPath = uri.getPath();
            }
        }
        return picPath;
    }

    /**
     * 抓取h5链接中的title
     *
     * @param mH5Title
     */
    public void setH5Title(String mH5Title) {

    }

    /**
     * 获取使用的WebViewClient, 方便进行一些加载监听
     *
     * @return
     */
    protected WebViewClient getWebViewClient() {
        return new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);
                return true;
            }
        };
    }

    /**
     * 是否允许运行JS
     *
     * @return
     */
    protected boolean enableJs() {
        return true;
    }

    /**
     * 允许缩放
     *
     * @return
     * @see #enableBuiltInZoomControls()
     */
    protected boolean enableScale() {
        return true;
    }

    /**
     * 使用放大缩小按钮, 应该和{@link #enableScale()}保持一致
     *
     * @return
     */
    protected boolean enableBuiltInZoomControls() {
        return true;
    }

    /**
     * 是否使用DOM storage API
     *
     * @return
     */
    protected boolean enableDomStorage() {
        return true;
    }

    /**
     * 滚动条风格，默认是不给滚动条留空间，滚动条覆盖在网页上
     *
     * @return
     */
    protected int getScrollBarStyle() {
        return WebView.SCROLLBARS_INSIDE_OVERLAY;
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
    protected int getCacheMode() {
        return WebSettings.LOAD_DEFAULT;
    }

    /**
     * 加载数据
     *
     * @param url
     */
    protected void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    /**
     * 是否可后退
     *
     * @return
     */
    protected boolean canGoBack() {
        return mWebView.canGoBack();
    }

    /**
     * 是否可前进
     *
     * @return
     */
    protected boolean canGoForward() {
        return mWebView.canGoForward();
    }

    /**
     * 后退
     */
    protected void goBack() {
        mWebView.goBack();
    }

    /**
     * 前进
     */
    protected void goForward() {
        mWebView.goForward();
    }

    /**
     * 获取progress bar的进度背景
     *
     * @return
     */
    protected Drawable getProgressBarDrawable() {
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
