package jx.csp.ui.activity.login;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;

/**
 * @auther WangLan
 * @since 2017/9/29
 */

public class YaYaAuthorizeLoginActivity extends BaseYaYaLoginActivity {


    @Override
    public void initData() {
    }

    @Override
    public void setViews() {
        super.setViews();
        // 清空用户信息
        Profile.inst().clear();
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addCloseIcon(bar, getString(R.string.yaya_authorization_login), this);
    }


}