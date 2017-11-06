package jx.csp.ui.activity.login;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.sp.SpApp;

/**
 * 丫丫医师授权登录
 * @auther WangLan
 * @since 2017/9/29
 */

public class YaYaAuthorizeLoginActivity extends BaseYaYaLoginActivity {

    @Override
    public void setViews() {
        super.setViews();
        mEtUsername.setText(SpApp.inst().getUserName());
        mEtUsername.setSelection(SpApp.inst().getUserName().length());
        // 清空用户信息
        Profile.inst().clear();
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.authorization_login);
    }
}