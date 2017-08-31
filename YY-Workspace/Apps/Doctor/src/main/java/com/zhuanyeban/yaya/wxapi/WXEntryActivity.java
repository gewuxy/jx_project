package com.zhuanyeban.yaya.wxapi;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import lib.network.model.NetworkResp;
import lib.wx.WXLoginApi;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Constants;
import yy.doctor.Constants.WXType;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.UserAPI;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.ui.activity.user.login.WXLoginActivity;

/**
 * 微信的回调, (根据applicationId回调)
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName().toString();
    private final int KLogin = 0;
    private final int KBind = 1;

    private IWXAPI mApi;

    @Override
    public void initData() {
        mApi = WXLoginApi.create(this, Constants.KAppId);

        try {
            mApi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_wx_entry;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        refresh(RefreshWay.embed);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        mApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            // 微信授权
            switch (resp.errCode) {
                case ErrCode.ERR_OK: {
                    // 用户同意
                    SendAuth.Resp r = (SendAuth.Resp) resp;
                    String code = r.code;
                    String state = r.state;
                    if (state.equals(WXType.login)) {
                        exeNetworkReq(KLogin, UserAPI.checkWxBind(code).build());
                    } else {
                        exeNetworkReq(KBind, UserAPI.bindWX().code(code).build());
                    }
                }
                break;
                // 其他不处理
                default: {
                    finish();
                }
                break;
            }
        } else {
            // 微信分享
            finish();
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Profile> r = (Result<Profile>) result;
        if (id == KLogin) {
            if (r.isSucceed()) {
                Profile login = r.getData();
                String openid = login.getString(TProfile.openid);
                if (TextUtil.isNotEmpty(openid)) {
                    // 没有绑定过微信, 绑定
                    WXLoginActivity.nav(WXEntryActivity.this, openid);
                } else {
                    // 绑定过微信, 登录
                    Profile.inst().update(r.getData());
                    SpUser.inst().updateProfileRefreshTime();
                    notify(NotifyType.login);
                    startActivity(MainActivity.class);
                }
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            if (r.isSucceed()) {
                Profile profile = r.getData();
                if (profile == null) {
                    showToast("绑定失败");
                } else {
                    notify(NotifyType.bind_wx, profile.getString(TProfile.wxNickname));
                    Profile.inst().update(profile);
                }
            } else {
                onNetworkError(id, r.getError());
            }
        }
        finish();
    }

}