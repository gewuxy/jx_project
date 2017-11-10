package jx.csp.presenter;

import android.widget.EditText;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.SetBindContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.BindPhoneActivity;
import jx.csp.util.Util;
import lib.network.model.interfaces.IResult;
import lib.yy.contract.BasePresenterImpl;
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
    public void onNetworkSuccess(int id, IResult r) {
        switch (id) {
            case KBindEmailCode: {
                getView().onStopRefresh();
                if (r.isSucceed()) {

                    getView().setBindEmailSuccessJump();
                    getView().onFinish();
                    App.showToast(R.string.setting_bind_email_succeed);
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KBindPhoneCode: {
                getView().onStopRefresh();
                if (r.isSucceed()) {
                    saveBindPhone();
                    getView().onFinish();
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KChangePwdCode: {
                getView().onStopRefresh();
                if (r.isSucceed()) {
                    getView().onFinish();
                    App.showToast(R.string.setting_change_pwd_succeed);
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KCaptchaCode: {
                getView().onStopRefresh();
                if (r.isSucceed()) {
                    // 获取验证码
                    getView().getCaptcha();
                    App.showToast(R.string.account_send_captcha);
                } else {
                    onNetworkError(id, r.getError());
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
    public void confirmBindAccount(int id, String userName, String num) {
        if (id == KBindEmailCode) {
            exeNetworkReq(id, UserAPI.bindEmail(userName, num).build());
        } else {
            exeNetworkReq(id, UserAPI.bindPhone(userName, num).build());
        }
    }

    @Override
    public void getCaptcha(int id, String userName, String type) {
        exeNetworkReq(id, UserAPI.sendCaptcha(userName, type).build());
    }

    @Override
    public void modifyPwd(int id, String oldPwd, String newPwd) {
        exeNetworkReq(id, UserAPI.changePwd(oldPwd, newPwd).build());
    }

    @Override
    public void saveBindPhone() {
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
