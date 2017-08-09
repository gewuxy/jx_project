package yy.doctor.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.util.Util;

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
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();
            notify(NotifyType.login);
            startActivity(MainActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
