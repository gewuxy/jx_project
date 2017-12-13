package com.zhuanyeban.yaya.wxapi;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.Constants;
import jx.doctor.Constants.WXType;
import jx.doctor.R;
import jx.doctor.ui.activity.user.login.WXLoginApi;

/**
 * 微信的回调, (根据applicationId回调)
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName().toString();

    private IWXAPI mApi;

    @Override
    public void initData() {
        mApi = WXLoginApi.create(this, Constants.KWXAppId);

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
                        notify(NotifyType.login, code);
                    } else {
                        notify(NotifyType.bind_wx, code);
                    }
                }
                break;
                // 其他不处理
            }
        } else {
            switch (resp.errCode) {
                case ErrCode.ERR_OK: {
                    // 同意
                    showToast("分享成功");
                }
                break;
                case ErrCode.ERR_USER_CANCEL: {
                    // 取消
                    showToast("取消分享");
                }
                break;
                case ErrCode.ERR_AUTH_DENIED: {
                    // 失败
                    showToast("分享失败");
                }
                break;
            }
        }
        finish();
    }

}