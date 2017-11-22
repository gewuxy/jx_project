package jx.csp.ui.activity.me.bind;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.contact.BindPhoneContract;
import jx.csp.model.form.Form;
import jx.csp.model.form.edit.EditCaptchaForm;
import jx.csp.presenter.BindPhonePresenterImpl;
import jx.csp.util.Util;
import lib.ys.util.TextUtil;
import lib.yy.model.form.BaseForm;
import lib.yy.notify.Notifier.NotifyType;

/**
 * 绑定手机号
 *
 * @auther Huoxuyu
 * @since 2017/9/26
 */
public class BindPhoneActivity extends BaseSetActivity {

    private View mDialog;

    private EditText mEtCaptcha;
    private EditText mEtPhone;

    private BindPhoneContract.V mPhoneView;
    private BindPhoneContract.P mPhonePresenter;

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        mPhoneView = new BindPhoneViewImpl();
        mPhonePresenter = new BindPhonePresenterImpl(mPhoneView);

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.bind_phone_number)
                .layout(R.layout.form_edit_bind_phone_number)
                .hint(R.string.input_phone_number));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.et_captcha)
                .related(RelatedId.bind_captcha)
                .drawable(R.drawable.login_ic_pwd)
                .textColorRes(R.color.bind_captcha_text_selector)
                .enable(false)
                .hint(R.string.input_captcha));
    }

    @Override
    public void setViews() {
        super.setViews();
        mEtPhone = getRelatedItem(RelatedId.bind_phone_number).getHolder().getEt();
        mEtCaptcha = getRelatedItem(RelatedId.bind_captcha).getHolder().getEt();

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
    protected void doSet() {
        mPhonePresenter.confirmBindAccount(RelatedId.bind_phone_number, getPhone(), getCaptcha(), null);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // 是手机号且验证码长度为6
        mView.setChanged(Util.isMobileCN(getPhone()) && TextUtil.isNotEmpty(getCaptcha()) && getCaptcha().length() == 6);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        switch ((int) related) {
            case RelatedId.bind_captcha: {
                if (v.getId() == R.id.form_tv_text) {
                    mPhonePresenter.checkMobile(getPhone());
                    mPhoneView.addItemCaptchaView();
                    mPhonePresenter.showCaptchaDialog(this, mDialog);
                }
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        BaseForm form = getRelatedItem(RelatedId.bind_captcha);
        if (type == NotifyType.fetch_message_captcha) {
            form.enable(true);
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            form.enable(false);
        }
        refreshItem(form);
    }

    private class BindPhoneViewImpl extends BaseSetBindViewImpl implements BindPhoneContract.V {

        @Override
        public void addItemCaptchaView() {
            mDialog = inflate(R.layout.dialog_captcha);
            TextView tv = mDialog.findViewById(R.id.captcha_tv_phone_number);
            String phone = getRelatedItem(RelatedId.bind_phone_number).getVal();
            tv.setText(phone);
        }


        @Override
        public void getCaptcha() {
            EditCaptchaForm item = (EditCaptchaForm) getRelatedItem(RelatedId.bind_captcha);
            item.start();
        }

        @Override
        public void closePage() {
            finish();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {

        }
    }
}
