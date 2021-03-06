package jx.csp.ui.activity.login.auth;

import android.annotation.SuppressLint;
import android.support.annotation.CallSuper;
import android.view.View;
import android.view.ViewTreeObserver;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.constant.Constants;
import jx.csp.model.Profile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.UIUtil;

/**
 * @auther WangLan
 * @since 2017/11/6
 */

abstract public class BaseAuthLoginActivity extends BaseActivity {

    private String mFrozen;

    @Override
    public void initData() {
        mFrozen = getIntent().getStringExtra(Constants.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @CallSuper
    @Override
    public void findViews() {
    }

    @SuppressLint("ResourceAsColor")
    @CallSuper
    @Override
    public void setViews() {
        UIUtil.setFlatBar(getWindow());
        getNavBar().setBackgroundColor(R.color.translucent);

        setOnClickListener(R.id.login_protocol);
        if (TextUtil.isNotEmpty(mFrozen)) {
            addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    UISetter.showFrozenDialog(mFrozen, BaseAuthLoginActivity.this);
                    removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    @CallSuper
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_protocol: {
                String agreement;
                if (Util.checkAppCn()) {
                    agreement = getString(R.string.service_agreement);
                }else {
                    agreement = getString(R.string.service_agreement_oversea);
                }
                CommonWebViewActivityRouter.create(UrlUtil.getUrlDisclaimer()).name(agreement)
                        .route(this);
            }
            break;
        }
    }

    public OnAuthListener newListener(int id, @BindId int type) {
        return new OnAuthListener() {

            @Override
            public void onAuthSuccess(AuthParams params) {
                String userGender = params.getGender();
                String icon = params.getIcon();
                String userId = params.getId();
                String userName = params.getName();
                // 微信专用，其他不变
                String uniqueId = params.getUnionId();
                if (type == BindId.wechat) {
                    exeNetworkReq(id, UserAPI.login(type)
                            .uniqueId(uniqueId)
                            .nickName(userName)
                            .gender(userGender)
                            .avatar(icon)
                            .build());
                } else {
                    exeNetworkReq(id, UserAPI.login(type)
                            .uniqueId(userId)
                            .nickName(userName)
                            .gender(userGender)
                            .avatar(icon)
                            .build());
                }
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
    public IResult onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    @Override
    public boolean interceptNetSuccess(int id, IResult r) {
        if (r.getCode() == Constants.KAccountFrozen) {
            stopRefresh();
            UISetter.showFrozenDialog(r.getMessage(), BaseAuthLoginActivity.this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }

}
