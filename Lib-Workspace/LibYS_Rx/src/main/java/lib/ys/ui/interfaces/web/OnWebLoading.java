package lib.ys.ui.interfaces.web;

import android.webkit.WebView;

/**
 * @auther yuansui
 * @since 2017/8/21
 */

public interface OnWebLoading {
    boolean loading(WebView view, String url);
}
