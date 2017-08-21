package lib.ys.ui.interfaces.web;

import android.webkit.WebView;

/**
 * @auther yuansui
 * @since 2017/8/21
 */

public interface OnWebError {
    void onError(WebView view, int errorCode, String description, String failingUrl);
}
