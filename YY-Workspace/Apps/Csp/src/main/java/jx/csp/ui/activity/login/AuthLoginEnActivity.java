package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.constant.LoginType;
import jx.csp.model.Profile;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.main.MainActivity;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;
import lib.yy.network.Result;

/**
 * @auther WangLan
 * @since 2017/11/6
 */
public class AuthLoginEnActivity extends BaseAuthLoginActivity {

    private final int KIdFaceBook = 3;
    private final int KIdTwitter = 4;

    private String mUserName;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_en;
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.login_facebook);
        setOnClickListener(R.id.login_twitter);
        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.login_jx);
        setOnClickListener(R.id.language_transform_en);
    }

    @Override
    protected int getVideoViewId() {
        return R.id.login_videoview;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.login_facebook: {
                Platform.auth(Type.facebook, newListener(KIdFaceBook, LoginType.facebook));
            }
            break;
            case R.id.login_twitter: {
                Platform.auth(Type.twitter, newListener(KIdTwitter, LoginType.twitter));
            }
            break;
            case R.id.login_jx: {
                startActivity(YaYaAuthorizeLoginActivity.class);
            }
            break;
            case R.id.language_transform_en: {
                startActivity(AuthLoginActivity.class);
                finish();
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
                showToast("取消");
            }
        };
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
        if (id == KIdFaceBook || id == KIdTwitter) {
            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                SpApp.inst().saveUserName(mUserName);
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
