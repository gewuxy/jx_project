package jx.csp.presenter;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.SetBindContract;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import lib.network.model.NetworkResp;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;

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

                    getView().bindEmailResult();
                    getView().onFinish();
                } else {
                    App.showToast(r.getMessage());
                }
            }
            break;
            case KBindPhoneCode: {
                if (r.isSucceed()) {
                    getView().bindPhoneResult();
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
}
