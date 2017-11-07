package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.main.MainActivity;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;

/**
 * 第三方登录
 *
 * @auther WangLan
 * @since 2017/9/27
 */

public class AuthLoginActivity extends BaseAuthLoginActivity {

    private final int KIdWechatLogin = 3;
    private final int KIdSinaLogin = 4;
    private final int KLoginVideo = 3;
    private final int KDownLoadVideo = 4;

    private String mUserName;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.layout_login_wechat);
        setOnClickListener(R.id.layout_login_sina);
        setOnClickListener(R.id.login_mobile);
        setOnClickListener(R.id.language_transform);
        setOnClickListener(R.id.layout_login_jx);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.layout_login_wechat: {
                Platform.auth(Type.wechat, newListener(KIdWechatLogin, LoginType.wechat));
            }
            break;
            case R.id.layout_login_sina: {
                Platform.auth(Type.sina, newListener(KIdSinaLogin, LoginType.sina));
            }
            break;
            case R.id.login_mobile: {
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.language_transform: {
                startActivity(AuthLoginEnActivity.class);
            }
            break;
            case R.id.layout_login_jx: {
                startActivity(YaYaAuthorizeLoginActivity.class);
            }
            break;
        }
    }

    private OnAuthListener newListener(int id, @LoginType int type) {
        return new OnAuthListener() {

            @Override
            public void onAuthSuccess(AuthParams params) {
                String userGender = params.getGender();
                String icon = params.getIcon();
                String userId = params.getId();
                mUserName = params.getName();

                exeNetworkReq(id, UserAPI.login(type)
                        .uniqueId(userId)
                        .nickName(mUserName)
                        .gender(userGender)
                        .avatar(icon)
                        .build());
                showToast("授权成功");
            }

            @Override
            public void onAuthError(String message) {
                showToast("失败: " + message);
            }

            @Override
            public void onAuthCancel() {
            }
        };
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
        if (id == KIdWechatLogin || id == KIdSinaLogin) {
            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                SpApp.inst().saveUserName(mUserName);
                Profile.inst().update(r.getData());
                SpUser.inst().updateProfileRefreshTime();
                startActivity(MainActivity.class);
                finish();
            }else {
                onNetworkError(id, r.getError());
            }
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }
}
