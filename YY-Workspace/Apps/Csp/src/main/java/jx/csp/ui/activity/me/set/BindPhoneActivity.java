package jx.csp.ui.activity.me.set;

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
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.model.form.edit.EditCaptchaForm;
import jx.csp.network.NetworkAPISetter.UserAPI;
import jx.csp.util.Util;

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

        int paddingLeft = fitDp(16);

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .layout(R.layout.form_edit_bind_phone_number)
                .hint(R.string.input_phone_number));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.et_captcha)
                .related(RelatedId.captcha)
                .drawable(R.drawable.login_ic_pwd)
                .paddingLeft(paddingLeft)
                .textColorRes(R.color.bind_captcha_text_selector)
                .enable(false)
                .hint(R.string.input_captcha));
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
        exeNetworkReq(UserAPI.bindPhone(getPhone(), getCaptcha(), "d48f972107584add99e48adc510fdb35").build());
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
                        showToast(R.string.account_is_bind);
                        return;
                    }
                    // 获取验证码(有倒计时,不用loading)
//                    exeNetworkReq(KCaptcha, UserAPI.sendCaptcha(getPhone(), KCaptcha + "","d48f972107584add99e48adc510fdb35").build());
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
                EditCaptchaForm item = (EditCaptchaForm) getRelatedItem(RelatedId.captcha);
                item.start();
                showToast(R.string.account_send_captcha);
            } else {
                // FIXME: 2017/9/27 手机号未成功显示在界面但保存了数据
                Profile.inst().put(TProfile.phone, getPhone());
                Profile.inst().saveToSp();

                notify(NotifyType.bind_phone, getPhone());
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