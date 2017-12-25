package jx.csp.ui.activity.me.bind;

import jx.csp.R;
import jx.csp.ui.activity.login.BaseYaYaLoginActivity;

/**
 * @auther HuoXuYu
 * @since 2017/10/30
 */

public class YaYaAuthorizeBindActivity extends BaseYaYaLoginActivity {

    @Override
    public void setViews() {
        super.setViews();
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.account_confirm_bind);
    }
}
