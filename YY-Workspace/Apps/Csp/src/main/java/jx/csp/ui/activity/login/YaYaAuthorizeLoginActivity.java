package jx.csp.ui.activity.login;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;

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

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            SpApp.inst().saveUserName(getUserName());
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();
            startActivity(MainActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }
}