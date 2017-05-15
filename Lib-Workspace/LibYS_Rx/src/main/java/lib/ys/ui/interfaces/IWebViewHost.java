package lib.ys.ui.interfaces;

import android.graphics.drawable.Drawable;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

/**
 * @auther yuansui
 * @since 2017/5/15
 */

public interface IWebViewHost {

    int getScrollBarStyle();

    /**
     * 是否使用DOM storage API
     *
     * @return
     */
    boolean enableDomStorage();

    /**
     * 使用放大缩小按钮, 应该和{@link #enableScale()}保持一致
     *
     * @return
     */
    boolean enableBuiltInZoomControls();

    /**
     * 允许缩放
     *
     * @return
     * @see #enableBuiltInZoomControls()
     */
    boolean enableScale();

    WebViewClient getWebViewClient();

    Drawable getProgressBarDrawable();

    /**
     * 是否允许运行JS
     *
     * @return
     */
    boolean enableJs();

    /**
     * 抓取h5链接中的title
     *
     * @param h5Title
     */
    void setH5Title(String h5Title);

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
    int getCacheMode();

    /**
     * 加载数据
     *
     * @param url
     */
    void loadUrl(String url);

    /**
     * 是否可后退
     *
     * @return
     */
    boolean canGoBack();

    /**
     * 是否可前进
     *
     * @return
     */
    boolean canGoForward();

    /**
     * 后退
     */
    void goBack();

    /**
     * 前进
     */
    void goForward();
}
