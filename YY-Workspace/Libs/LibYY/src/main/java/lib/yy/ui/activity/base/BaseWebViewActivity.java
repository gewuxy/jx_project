package lib.yy.ui.activity.base;


import lib.ys.action.IntentAction;
import lib.ys.ui.activity.WebViewActivityEx;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.interfaces.impl.WebOption;
import lib.yy.model.WebAction;
import lib.yy.model.WebAction.TWebAction;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
abstract public class BaseWebViewActivity extends WebViewActivityEx {

    private static final String KJXPrefix = "jx://";

    private String mFailingUrl;

    @Override
    public void setViews() {
        super.setViews();

        setViewState(ViewState.loading);
    }

    @Override
    public WebOption getOption() {
        return WebOption.newBuilder()
                .onSuccess((view, url) -> setViewState(ViewState.normal))
                .onError((view, errorCode, description, failingUrl) -> {
                    setViewState(ViewState.error);
                    mFailingUrl = failingUrl;
                })
                .onLoading((view, url) -> {
                    if (url.startsWith(KJXPrefix)) {
                        String json = url.substring(KJXPrefix.length());
                        WebAction action = new WebAction();
                        action.parse(json);

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
                })
                .build();
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            setViewState(ViewState.loading);
            loadUrl(mFailingUrl);
        }
        return true;
    }
}
