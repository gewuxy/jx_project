package jx.csp.ui.activity;

import android.os.Bundle;

import java.util.HashMap;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.util.UISetter;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseWebViewActivity;

/**
 * WebView页面
 *
 * @author CaiXiang
 * @since 2017/6/16
 */
@Route
public class CommonWebViewActivity extends BaseWebViewActivity {

    @Arg(opt = true)
    String mName;

    @Arg
    String mUrl;

    @Arg(opt = true)
    HashMap<String, String> mHeaders;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.drawable.nav_bar_ic_back, this);

        UISetter.setNavBarMidText(bar, mName, this);
    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl, mHeaders);
    }

}
