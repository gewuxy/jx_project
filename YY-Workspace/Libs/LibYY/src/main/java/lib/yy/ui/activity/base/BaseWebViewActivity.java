package lib.yy.ui.activity.base;


import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;

import lib.ys.YSLog;
import lib.ys.ui.activity.WebViewActivityEx;
import lib.ys.util.IntentAction;
import lib.yy.model.WebAction;
import lib.yy.model.WebAction.TWebAction;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
abstract public class BaseWebViewActivity extends WebViewActivityEx {

    private static final String KJXPrefix = "jx://";

    @Override
    public WebViewClient getWebViewClient() {
        return new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if (url.startsWith(KJXPrefix)) {
                    String json = url.substring(KJXPrefix.length());
                    WebAction action = new WebAction();
                    try {
                        action.parse(json);
                    } catch (JSONException e) {
                        YSLog.e(TAG, "shouldOverrideUrlLoading", e);
                    }

                    // 暂时只有邮箱
                    if (action.getString(TWebAction.action).equals("mail")) {
                        IntentAction.mail()
                                .address(action.getString(TWebAction.data))
                                .alert("没有邮件类应用")
                                .launch();
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        };
    }
}
