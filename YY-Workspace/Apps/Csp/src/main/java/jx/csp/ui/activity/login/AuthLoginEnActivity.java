package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;
import lib.ys.ui.other.NavBar;

/**
 * @auther WangLan
 * @since 2017/11/6
 */
public class AuthLoginEnActivity extends BaseAuthLoginActivity {

    private final int KIdFaceBook = 1;
    private final int KIdTwitter = 2;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_en;
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.login_facebook);
        setOnClickListener(R.id.login_twitter);
        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.login_jx);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_facebook: {
                Platform.auth(Type.facebook, newListener(KIdFaceBook, LoginType.facebook_login));
            }
            break;
            case R.id.login_twitter: {
                Platform.auth(Type.twitter, newListener(KIdTwitter, LoginType.twitter_login));
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
                String userName = params.getName();

                exeNetworkReq(id, UserAPI.login(type)
                        .uniqueId(userId)
                        .nickName(userName)
                        .gender(userGender)
                        .avatar(icon)
                        .build());
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
}
