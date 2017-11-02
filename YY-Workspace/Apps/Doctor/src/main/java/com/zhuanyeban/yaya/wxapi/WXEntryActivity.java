package com.zhuanyeban.yaya.wxapi;

import android.content.Intent;
import android.widget.Toast;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

/**
 * 微信的回调, (根据applicationId回调)
 */
public class WXEntryActivity extends WechatHandlerActivity {

    @Override
    public void onGetMessageFromWXReq(WXMediaMessage msg) {
        if (msg != null) {
            Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
            startActivity(iLaunchMyself);
        }
    }

    @Override
    public void onShowMessageFromWXReq(WXMediaMessage msg) {
        if (msg != null && msg.mediaObject != null
                && (msg.mediaObject instanceof WXAppExtendObject)) {
            WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
            Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
        }
    }


    /*private static final String TAG = WXEntryActivity.class.getSimpleName().toString();
    private final int KLogin = 0;
    private final int KBind = 1;

    private IWXAPI mApi;

    @Override
    public void initData() {
//        mApi = WXLoginApi.create(this, Constants.KAppId);

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
            switch (resp.errCode) {
                case ErrCode.ERR_OK:
                    showToast(ShareDialog.KShareSuccess);
                    break;
                case ErrCode.ERR_USER_CANCEL:
                    showToast(ShareDialog.KShareCancel);
                    break;
                case ErrCode.ERR_SENT_FAILED:
                    showToast(ShareDialog.KShareError.concat(resp.errStr));
                    break;
            }
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
    }*/

}