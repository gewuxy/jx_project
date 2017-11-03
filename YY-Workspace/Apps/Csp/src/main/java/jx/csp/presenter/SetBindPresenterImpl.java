package jx.csp.presenter;

import android.widget.EditText;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.SetBindContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.BindPhoneActivity;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;

/**
 * @auther Huoxuyu
 * @since 2017/10/31
 */

public class SetBindPresenterImpl extends BasePresenterImpl<SetBindContract.V> implements SetBindContract.P {

    private final int KBindEmailCode = 0;
    private final int KBindPhoneCode = 1;
    private final int KCaptchaCode = 3;
    private final int KChangePwdCode = 4;

    public SetBindPresenterImpl(SetBindContract.V v) {
        super(v);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        getView().onStopRefresh();
        switch (id) {
            case KBindEmailCode: {
                if (r.isSucceed()) {
                    App.showToast(R.string.setting_bind_email_succeed);

                    getView().setBindEmailSuccessJump();
                    getView().onFinish();
                } else {
                    App.showToast(r.getMessage());
                }
            }
            break;
            case KBindPhoneCode: {
                if (r.isSucceed()) {
                    setSaveBindPhone();
                    getView().onFinish();
                } else {
                    App.showToast(r.getMessage());
                }
            }
            break;
            case KChangePwdCode: {
                if (r.isSucceed()) {
                    App.showToast(R.string.setting_change_pwd_succeed);
                    getView().onFinish();
                } else {
                    App.showToast(r.getMessage());
                }
            }
            break;
            case KCaptchaCode: {
                if (r.isSucceed()) {
                    // 获取验证码
                    getView().getCaptcha();
                    App.showToast(R.string.account_send_captcha);
                }else {
                    App.showToast(r.getMessage());
                }
            }
            break;
        }
    }

    @Override
    public void checkPwd(EditText et) {
        if (!Util.checkPwd(Util.getEtString(et))) {
            return;
        }
    }

    @Override
    public void confirmBindNetworkReq(int id, String userName, String num) {
        if (id == KBindEmailCode) {
            exeNetworkReq(id, UserAPI.bindEmail(userName, num).build());
        } else {
            exeNetworkReq(id, UserAPI.bindPhone(userName, num).build());
        }
    }

    @Override
    public void getCaptchaNetworkReq(int id, String userName, String type) {
        exeNetworkReq(id, UserAPI.sendCaptcha(userName, type).build());
    }

    @Override
    public void changePwdNetworkReq(int id, String oldPwd, String newPwd) {
        exeNetworkReq(id, UserAPI.changePwd(oldPwd, newPwd).build());
    }

    @Override
    public void setSaveBindPhone() {
        Profile.inst().put(TProfile.mobile, BindPhoneActivity.getPhone());
        Profile.inst().saveToSp();
        Notifier.inst().notify(NotifyType.profile_change, BindPhoneActivity.getPhone());
        Notifier.inst().notify(NotifyType.bind_phone, BindPhoneActivity.getPhone());
    }

    @Override
    public void equalsMobile() {
        if (BindPhoneActivity.getPhone().equals(Profile.inst().getString(TProfile.mobile))) {
            App.showToast(R.string.account_is_bind);
            return;
        }
    }
}