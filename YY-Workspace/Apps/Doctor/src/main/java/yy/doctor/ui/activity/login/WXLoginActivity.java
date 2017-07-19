package yy.doctor.ui.activity.login;

import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * @auther Huoxuyu
 * @since 2017/7/12
 */

public class WXLoginActivity extends BaseLoginActivity {

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_wx;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.wx_binding, this);
    }

    @Override
    protected CharSequence getBtnText() {
        return "绑定并登录";
    }

    @Override
    public void btnClick(String pwd) {

    }
}
