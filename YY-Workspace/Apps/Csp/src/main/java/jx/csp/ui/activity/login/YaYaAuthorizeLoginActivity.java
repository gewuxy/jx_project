package jx.csp.ui.activity.login;

import jx.csp.Extra;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.TestActivity;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;

/**
 * @auther WangLan
 * @since 2017/9/29
 */

public class YaYaAuthorizeLoginActivity extends BaseYaYaLoginActivity {

    private String mRequest; // 判断桌面快捷方式进来

    @Override
    public void initData() {
        mRequest = getIntent().getStringExtra(Extra.KData);
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
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            //保存用户名，邮箱用户名是昵称？？？
            SpApp.inst().saveUserName(getUserName());
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();

            //判断跳转到哪里
            if (TextUtil.isEmpty(mRequest)) {
                //Fixme:跳转到首页，目前暂时没有
                startActivity(TestActivity.class);
            } else {
                setResult(RESULT_OK);
            }
            stopRefresh();
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }
}