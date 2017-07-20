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

import org.json.JSONObject;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.JsonUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Constants;
import yy.doctor.R;
import yy.doctor.model.WXLogin;
import yy.doctor.model.WXLogin.TWXLogin;
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
                // fixme:告诉后台code
                exeNetworkReq(NetFactory.getWXToken("b02f7292152660b7b551a70dced8feec", code));
            }
            break;
            // 其他不处理
        }
        finish();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (r == null && TextUtil.isEmpty(r.getText())) {
            return null;
        }
        JSONObject obj = new JSONObject(r.getText());
        Result<WXLogin> result = new Result<>();
        JsonUtil.setEV(WXLogin.class, result, obj);
        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<WXLogin> r = (Result<WXLogin>) result;
        if (r != null) {
            WXLogin login = r.getData();
            String accessToken = login.getString(TWXLogin.access_token);
            String openId = login.getString(TWXLogin.openid);
            String unionId = login.getString(TWXLogin.unionid);
            String refreshToken = login.getString(TWXLogin.refresh_token);
            YSLog.d(TAG, "onNetworkSuccess:accessToken" + accessToken);
            YSLog.d(TAG, "onNetworkSuccess:openId" + openId);
            YSLog.d(TAG, "onNetworkSuccess:unionId" + unionId);
            YSLog.d(TAG, "onNetworkSuccess:refreshToken" + refreshToken);
            if (login.getBoolean(TWXLogin.refresh_token)) {
                // 没有绑定过微信, 绑定
                startActivity(new Intent(WXEntryActivity.this, WXLoginActivity.class));
            } else {
                // 绑定过微信, 登录

            }
        }
    }
}