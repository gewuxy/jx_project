package jx.csp.ui.activity.login.auth;

import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.model.Profile;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.MainActivity;
import jx.csp.ui.activity.login.YaYaAuthorizeActivity;
import jx.csp.ui.activity.login.email.EmailLoginActivity;
import jx.csp.ui.activity.login.moblie.CaptchaLoginActivity;
import lib.jx.network.Result;
import lib.network.model.interfaces.IResult;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.ys.config.AppConfig.RefreshWay;

/**
 * 第三方登录
 *
 * @auther WangLan
 * @since 2017/9/27
 */

public class AuthLoginActivity extends BaseAuthLoginActivity {

    private final int KIdWechatLogin = 3;
    private final int KIdSinaLogin = 4;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.layout_login_wechat);
        setOnClickListener(R.id.layout_login_weibo);
        setOnClickListener(R.id.layout_login_mobile);
        setOnClickListener(R.id.layout_login_email);
        setOnClickListener(R.id.layout_login_jingxin);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.layout_login_wechat: {
                refresh(RefreshWay.dialog);
                Platform.auth(Type.wechat, newListener(KIdWechatLogin, BindId.wechat));
                stopRefresh();
            }
            break;
            case R.id.layout_login_weibo: {
                refresh(RefreshWay.dialog);
                Platform.auth(Type.sina, newListener(KIdSinaLogin, BindId.sina));
                stopRefresh();
            }
            break;
            case R.id.layout_login_mobile: {
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.layout_login_email: {
                startActivity(EmailLoginActivity.class);
            }
            break;
            case R.id.layout_login_jingxin: {
                startActivity(YaYaAuthorizeActivity.class);
            }
            break;
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult result) {
        super.onNetworkSuccess(id, result);
        if (id == KIdWechatLogin || id == KIdSinaLogin) {
            stopRefresh();
            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                SpUser.inst().updateProfileRefreshTime();
                startActivity(MainActivity.class);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }
}
