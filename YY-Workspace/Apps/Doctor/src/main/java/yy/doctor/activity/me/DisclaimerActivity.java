package yy.doctor.activity.me;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.util.Util;

/**
 * 免责声明
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class DisclaimerActivity extends BaseWebViewActivity {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "免责声明", this);
    }

    @Override
    protected void onLoadStart() {

    }
}
