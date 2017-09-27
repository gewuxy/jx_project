package jx.csp.ui.activity;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseWebViewActivity;

/**
 * WebView页面
 *
 * @author CaiXiang
 * @since 2017/6/16
 */
@Route
public class CommonWebViewActivity extends BaseWebViewActivity {

    @Arg()
    String mName;

    @Arg()
    String mUrl;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
//        UISetter.setNavBarMidText(bar, mName, this);
    }

    @Override
    public void setViews() {
        super.setViews();
    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl);
    }

}
