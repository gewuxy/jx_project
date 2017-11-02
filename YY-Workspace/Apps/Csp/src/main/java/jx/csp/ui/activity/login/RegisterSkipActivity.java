package jx.csp.ui.activity.login;

import jx.csp.R;

/**
 * 注册的跳转界面
 * @auther WangLan
 * @since 2017/10/17
 */

public class RegisterSkipActivity extends BaseSkipActivity{
    @Override
    protected CharSequence getTitleText() {
        return getString(R.string.register);
    }

    @Override
    protected CharSequence getSkipText() {
        return getString(R.string.active_email);
    }
}
