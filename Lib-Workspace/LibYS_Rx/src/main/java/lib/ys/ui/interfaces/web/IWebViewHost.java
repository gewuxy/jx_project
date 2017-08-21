package lib.ys.ui.interfaces.web;

import lib.ys.ui.interfaces.impl.WebOption;

/**
 * @auther yuansui
 * @since 2017/5/15
 */

public interface IWebViewHost {

    WebOption getOption();

    /**
     * 抓取h5链接中的title
     *
     * @param h5Title
     */
    void setH5Title(String h5Title);

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
