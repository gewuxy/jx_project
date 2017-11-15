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
import lib.ys.config.AppConfig.RefreshWay;
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

    private String mUserName;

    private View mLayoutCn;
    private View mLayoutEn;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
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

        setOnClickListener(R.id.layout_login_wechat);
        setOnClickListener(R.id.layout_login_sina);
        setOnClickListener(R.id.login_mobile);
        setOnClickListener(R.id.layout_login_jx);
        if (SpApp.inst().getLangType() == LangType.en) {
            goneView(mLayoutCn);
            showView(mLayoutEn);
            setOnClickListener(R.id.login_en_protocol);
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
            case R.id.layout_login_wechat: {
                refresh(RefreshWay.dialog);
                Platform.auth(Type.wechat, newListener(KIdWechatLogin, BindId.wechat));
                stopRefresh();
            }
            break;
            case R.id.layout_login_sina: {
                refresh(RefreshWay.dialog);
                Platform.auth(Type.sina, newListener(KIdSinaLogin, BindId.sina));
                stopRefresh();
            }
            break;
            case R.id.login_mobile: {
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.layout_login_jx: {
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

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }
}
