package jx.csp.ui.activity.login;

import jx.csp.R;

/**
 * @auther WangLan
 * @since 2017/9/28
 */

public class ForgetPwdSkipActivity extends BaseSkipActivity{
    @Override
    protected CharSequence getTitleText() {
        return getString(R.string.find_pwd);
    }

    @Override
    protected CharSequence getSkipText() {
        return getString(R.string.reset_pwd_to_email);
    }
}
