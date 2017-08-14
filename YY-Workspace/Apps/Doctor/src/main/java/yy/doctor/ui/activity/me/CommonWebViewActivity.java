package yy.doctor.ui.activity.me;

import router.annotation.AutoIntent;
import router.annotation.Extra;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseWebViewActivity;
import yy.doctor.util.Util;

/**
 * WebView页面
 *
 * @author CaiXiang
 * @since 2017/6/16
 */
@AutoIntent
public class CommonWebViewActivity extends BaseWebViewActivity {

    @Extra
    String mName;

    @Extra
    String mUrl;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mName, this);
    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl);
    }

}
