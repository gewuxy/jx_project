package jx.doctor.ui.activity.me.set;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.jx.model.form.BaseForm;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.Constants.CaptchaType;
import jx.doctor.R;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.form.Form;
import jx.doctor.model.form.FormType;
import jx.doctor.model.form.edit.EditCaptchaForm;
import jx.doctor.network.NetworkApiDescriptor.RegisterAPI;
import jx.doctor.network.NetworkApiDescriptor.UserAPI;
import jx.doctor.util.Util;

/**
 * 绑定手机号
 *
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class BindPhoneActivity extends BaseSetActivity {

    private final int KCaptcha = 0;

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
    public void initData(Bundle state) {
        super.initData(state);

        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .layout(R.layout.form_edit_bind_phone_number)
                .textWatcher(this)
                .hint("输入手机号码"));

        addItem(Form.create(FormType.divider));

        addItem(Form.create(FormType.et_captcha)
                .related(RelatedId.captcha)
                .layout(R.layout.form_edit_bind_captcha)
                .textColorRes(R.color.register_captcha_text_selector)
                .textWatcher(this)
                .hint(R.string.captcha)
                .enable(false));

        addItem(Form.create(FormType.divider));
    }

    @Override
    protected CharSequence getNavBarText() {
        return "绑定手机号";
    }

    @Override
    protected CharSequence getSetText() {
        return "确认绑定";
    }

    @NonNull
    private String getPhone() {
        return getRelatedString(RelatedId.phone_number).replace(" ", "");
    }

    @NonNull
    private String getCaptcha() {
        return getRelatedString(RelatedId.captcha);
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.bindMobile(getPhone(), getCaptcha()).build());
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
                    if (getPhone().equals(Profile.inst().getString(TProfile.mobile))) {
                        showToast("该手机号已绑定");
                        return;
                    }
                    // 获取验证码(有倒计时,不用loading)
                    exeNetworkReq(KCaptcha, RegisterAPI.captcha(getPhone(), CaptchaType.fetch).build());
                }
            }
            break;
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            if (id == KCaptcha) {
                // 获取验证码
                EditCaptchaForm item = (EditCaptchaForm) getRelatedItem(RelatedId.captcha);
                item.start();
            } else {
                notify(NotifyType.bind_phone, getPhone());
                Profile.inst().put(TProfile.mobile, getPhone());
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