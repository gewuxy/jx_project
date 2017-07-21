package com.zhuanyeban.yaya.wxapi;

import android.content.Intent;
import android.support.annotation.NonNull;


import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.BaseResp.ErrCode;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Constants;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.login.WXLoginActivity;

/**
 * 微信的回调, (根据applicationId回调)
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName().toString();

    private IWXAPI mApi;

    @Override
    public void initData() {
        mApi = WXAPIFactory.createWXAPI(this, Constants.KAppId, false);

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
        switch (resp.errCode) {
            case ErrCode.ERR_OK: {
                // 用户同意
                YSLog.d(TAG, "用户同意");
                SendAuth.Resp r = (SendAuth.Resp) resp;
                String code = r.token;
                String state = r.state;
                YSLog.d(TAG, "onResp:code" + code);
                YSLog.d(TAG, "onResp:state" + state);
                exeNetworkReq(NetFactory.check_wx_bind(code));
            }
            break;
            // 其他不处理
            default: {
                finish();
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            Profile login = r.getData();

            String openid = login.getString(TProfile.openid, "");
            YSLog.d(TAG, "onNetworkSuccess:openid" + openid);
            if (TextUtil.isNotEmpty(openid)) {
                // 没有绑定过微信, 绑定
                WXLoginActivity.nav(WXEntryActivity.this,openid);
            } else {
                // 绑定过微信, 登录

            }
        } else {
            showToast(r.getError());
        }
        finish();
    }
}