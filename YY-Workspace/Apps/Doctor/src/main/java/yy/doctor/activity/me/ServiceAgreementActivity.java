package yy.doctor.activity.me;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.util.Util;

/**
 * 服务协议
 *
 * @author CaiXiang
 * @since 2017/6/12
 */

public class ServiceAgreementActivity extends BaseWebViewActivity {

    private String mUrl;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "服务协议", this);
    }

    @Override
    protected void onLoadStart() {

    }
}
