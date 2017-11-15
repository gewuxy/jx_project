package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.constant.LangType;
import jx.csp.model.Profile;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.main.MainActivity;
import lib.network.model.interfaces.IResult;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;

/**
 * @auther WangLan
 * @since 2017/11/6
 */
public class AuthLoginOverseaActivity extends BaseAuthLoginActivity {

    private final int KIdFaceBook = 3;
    private final int KIdTwitter = 4;

    private String mUserName;
    private View mLayoutCn;
    private View mLayoutEn;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_en;
    }

    @Override
    public void findViews() {
        super.findViews();
        mLayoutCn = findView(R.id.layout_login_protocol_cn);
        mLayoutEn = findView(R.id.layout_login_protocol_en);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.login_facebook);
        setOnClickListener(R.id.login_twitter);
        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.login_jx);
        setOnClickListener(R.id.login_en_protocol);
        if (SpApp.inst().getLangType() != LangType.en) {
            goneView(mLayoutEn);
            showView(mLayoutCn);
        }
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
                Platform.auth(Type.facebook, newListener(KIdFaceBook, BindId.facebook));
            }
            break;
            case R.id.login_twitter: {
                Platform.auth(Type.twitter, newListener(KIdTwitter, BindId.twitter));
            }
            break;
            case R.id.login_jx: {
                startActivity(YaYaAuthorizeLoginActivity.class);
            }
            break;
            case R.id.login_en_protocol: {
                CommonWebViewActivityRouter.create(UrlUtil.getUrlDisclaimer()).name(getString(R.string.service_agreement))
                        .route(this);
            }
            break;
        }
    }

    private OnAuthListener newListener(int id, @BindId int type) {
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
                showToast(R.string.auth_success);
            }

            @Override
            public void onAuthError(String message) {
                showToast(R.string.auth_fail + message);
            }

            @Override
            public void onAuthCancel() {
            }
        };
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        super.onNetworkSuccess(id, r);
        if (id == KIdFaceBook || id == KIdTwitter) {
            if (r.isSucceed()) {
                Profile.inst().update((Profile) r.getData());
                SpUser.inst().updateProfileRefreshTime();
                startActivity(MainActivity.class);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }
}
