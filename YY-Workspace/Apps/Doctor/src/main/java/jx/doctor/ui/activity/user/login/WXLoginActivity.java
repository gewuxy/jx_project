package jx.doctor.ui.activity.user.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import lib.network.model.interfaces.IResult;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.Profile;
import jx.doctor.sp.SpUser;
import jx.doctor.ui.activity.MainActivity;
import jx.doctor.util.Util;

/**
 * @auther Huoxuyu
 * @since 2017/7/12
 */

public class WXLoginActivity extends BaseLoginActivity {

    public String mOpenId;

    public static void nav(Context context, String openId) {
        Intent i = new Intent(context, WXLoginActivity.class)
                .putExtra(Extra.KData, openId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mOpenId = getIntent().getStringExtra(Extra.KData);
    }

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

    @NonNull
    @Override
    protected String getOpenId() {
        return mOpenId;
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            Profile.inst().update((Profile) r.getData());
            SpUser.inst().updateProfileRefreshTime();
            notify(NotifyType.login);
            startActivity(MainActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
