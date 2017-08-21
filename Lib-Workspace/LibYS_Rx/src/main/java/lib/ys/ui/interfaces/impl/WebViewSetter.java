package lib.ys.ui.interfaces.impl;

import android.app.Activity;
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
import android.widget.ProgressBar;

import java.io.File;

import lib.ys.AppEx;
import lib.ys.ui.interfaces.web.IWebViewHost;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;


/**
 * @auther yuansui
 * @since 2017/5/15
 */

public class WebViewSetter {

    private IWebViewHost mHost;

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMsgs;

    public WebViewSetter(IWebViewHost host) {
        if (host == null) {
            throw new NullPointerException("host can not be null");
        }

        mHost = host;
    }

    public void set(WebView webView, ProgressBar bar) {
        WebOption option = mHost.getOption();

        Drawable pd = option.getProgressBarDrawable();
        if (pd != null) {
            /*
             * 这里必须使用ClipDrawable, 不然的话效果会变成整个drawable平铺, 看不出来进度了
			 */
            ClipDrawable d = new ClipDrawable(pd, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            bar.setProgressDrawable(d);
        }

		/*
         * 设置web view的属性
		 */
        WebSettings settings = webView.getSettings();

        settings.setCacheMode(option.getCacheMode());
        settings.setJavaScriptEnabled(option.isJsEnabled());

//        settings.setUseWideViewPort(option.isScaleEnabled());
        settings.setLoadWithOverviewMode(option.isScaleEnabled());

        settings.setBuiltInZoomControls(option.isBuiltInZoomControlsEnabled());
        settings.setDomStorageEnabled(option.isDomStorageEnabled());
        settings.setAllowFileAccess(true);

        webView.setScrollBarStyle(option.getScrollBarStyle());

        webView.setWebViewClient(option.getClient());

        // 自己管理进度条的展现形式, 暂时不对外开放
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    ViewUtil.goneView(bar);
                } else {
                    ViewUtil.showView(bar);
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mHost.setH5Title(title);
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
    }

    /**
     * webview上传文件
     *
     * @param uploadMsg
     */
    @JavascriptInterface
    public void updateFile(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        LaunchUtil.startActivityForResult(mHost, Intent.createChooser(intent, "选择要使用的应用"), 1);
    }

    /**
     * webview上传文件5.0以上
     *
     * @param uploadMsg
     */
    @JavascriptInterface
    public void updateFileForLollipop(ValueCallback<Uri[]> uploadMsg) {
        mUploadMsgs = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        LaunchUtil.startActivityForResult(mHost, Intent.createChooser(intent, "选择要使用的应用"), 2);
    }

    public void onResultData(int requestCode, int resultCode, Intent data) {
        // 不能写此判断，不能调到onReceiveValue()，导致再次点击添加图片无效
        // if (resultCode != RESULT_OK) {1
        // return;
        // }
        if (requestCode == 1) {
            if (null != mUploadMessage) {
                Uri result = data == null || resultCode != Activity.RESULT_OK ? null : Uri.fromFile(new File(decodePicUri(data.getData())));
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

    public void setOption(WebOption op) {

    }
}
