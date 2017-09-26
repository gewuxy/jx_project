package yaya.csp.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.model.form.BaseForm;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yaya.csp.R;
import yaya.csp.model.Profile;
import yaya.csp.model.Profile.TProfile;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;
import yaya.csp.network.NetworkAPISetter.UserAPI;
import yaya.csp.util.Util;

/**
 * 绑定手机号
 *
 * @auther Huoxuyu
 * @since 2017/9/26
 */
public class BindPhoneActivity extends BaseSetActivity {

    private final int KCaptcha = 1;

    private EditText mEtCaptcha;
    private EditText mEtPhone;

    @IntDef({
            RelatedId.phone_number,
            RelatedId.captcha,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int phone_number = 0;
        int captcha = 1;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et)
                .related(RelatedId.phone_number));

        addItem(Form.create(FormType.et)
                .related(RelatedId.captcha)
                .enable(false));

    }

    @Override
    public void setViews() {
        super.setViews();

        mEtPhone = getRelatedItem(RelatedId.phone_number).getHolder().getEt();
        mEtCaptcha = getRelatedItem(RelatedId.captcha).getHolder().getEt();

        mEtPhone.addTextChangedListener(this);
        mEtCaptcha.addTextChangedListener(this);
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.account_bind_phone);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.account_confirm_bind);
    }

    @NonNull
    private String getPhone() {
        return Util.getEtString(mEtPhone).replace(" ", "");
    }

    @NonNull
    private String getCaptcha() {
        return Util.getEtString(mEtCaptcha);
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.bindPhone(getPhone(), getCaptcha()).build());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // 是手机号且验证码长度为6
        setChanged(Util.isMobileCN(getPhone()) && TextUtil.isNotEmpty(getCaptcha()) && getCaptcha().length() == 6);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        switch ((int) related) {
            case RelatedId.captcha: {
                if (v.getId() == R.id.form_tv_text) {
                    if (getPhone().equals(Profile.inst().getString(TProfile.phone))) {
                        showToast("该手机号已绑定");
                        return;
                    }
                    // 获取验证码(有倒计时,不用loading)
//                    exeNetworkReq(KCaptcha, RegisterAPI.captcha(getPhone(), CaptchaType.re_fetch).build());
                }
            }
            break;
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result r = (Result) result;
        if (r.isSucceed()) {
            if (id == KCaptcha) {
                // 获取验证码
//                EditCaptchaForm item = (EditCaptchaForm) getRelatedItem(RelatedId.captcha);
//                item.start();
            } else {
                notify(NotifyType.bind_phone, getPhone());
                Profile.inst().put(TProfile.phone, getPhone());
                Profile.inst().saveToSp();
                finish();
            }
        } else {
            showToast(r.getMessage());
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        BaseForm form = getRelatedItem(RelatedId.captcha);
        if (type == NotifyType.fetch_message_captcha) {
            form.enable(true);
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            form.enable(false);
        }
        refreshItem(form);
    }
}