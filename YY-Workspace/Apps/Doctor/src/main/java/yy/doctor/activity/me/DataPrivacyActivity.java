package yy.doctor.activity.me;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.util.Util;

/**
 * 隐私保密
 *
 * @author CaiXiang
 * @since 2017/6/12
 */

public class DataPrivacyActivity extends BaseWebViewActivity {

    private String mUrl;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "隐私保密", this);
    }

    @Override
    protected void onLoadStart() {

    }
}
