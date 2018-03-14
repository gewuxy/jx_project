package jx.csp.presenter;

import android.content.Context;

import jx.csp.contact.SettingsContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.ui.activity.login.auth.AuthLoginActivity;
import jx.csp.ui.activity.login.auth.AuthLoginOverseaActivity;
import jx.csp.ui.activity.me.SettingsActivity;
import jx.csp.ui.activity.me.bind.BindEmailTipsActivity;
import jx.csp.ui.activity.me.bind.ChangePwdActivity;
import jx.csp.util.Util;
import lib.jx.contract.BasePresenterImpl;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;

/**
 * @auther HuoXuYu
 * @since 2017/10/26
 */

public class SettingsPresenterImpl extends BasePresenterImpl<SettingsContract.V>
        implements SettingsContract.P {

    public SettingsPresenterImpl(SettingsContract.V v) {
        super(v);
    }

    @Override
    public void changePassWord() {
        if (TextUtil.isEmpty(Profile.inst().getString(TProfile.email))) {
            LaunchUtil.startActivity(SettingsActivity.class, BindEmailTipsActivity.class);
        } else {
            //已绑定邮箱,直接跳转到修改页面
            LaunchUtil.startActivity(SettingsActivity.class, ChangePwdActivity.class);
        }
    }

    @Override
    public void startLogoutService(Context context) {
        CommonServRouter.create(ReqType.logout).route(context);
    }

    @Override
    public void logout(Context context) {
        startLogoutService(context);

        if (Util.checkAppCn()) {
            LaunchUtil.startActivity(context, AuthLoginActivity.class);
        } else {
            LaunchUtil.startActivity(context, AuthLoginOverseaActivity.class);
        }
        getView().closePage();
    }
}
