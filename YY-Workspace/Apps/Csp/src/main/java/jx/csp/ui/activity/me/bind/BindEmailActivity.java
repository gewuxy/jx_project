package jx.csp.ui.activity.me.bind;

import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.model.form.Form;
import jx.csp.util.Util;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;

/**
 * 认证邮箱
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class BindEmailActivity extends BaseSetActivity {

    private EditText mEtEmail;
    private EditText mEtPwd;

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et)
                .related(RelatedId.bind_email)
                .hint(R.string.setting_input_email_address)
                .layout(R.layout.form_edit_bind_email));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd)
                .hint(R.string.input_pwd)
                .drawable(R.drawable.login_selector_visible));
    }

    @Override
    public void setViews() {
        super.setViews();
        mEtEmail = getRelatedItem(RelatedId.bind_email).getHolder().getEt();
        mEtPwd = getRelatedItem(RelatedId.pwd).getHolder().getEt();

        mEtEmail.addTextChangedListener(this);
        mEtPwd.addTextChangedListener(this);
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.setting_bind_email);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.setting_certification_email);
    }

    @Override
    protected void doSet() {
        mPresenter.checkPwd(mEtPwd);
        mPresenter.confirmBindAccount(RelatedId.bind_email, getEmail(), getUserPwd());
    }

    @Override
    public void afterTextChanged(Editable s) {
        mView.setChanged(RegexUtil.isEmail(getEmail()) && TextUtil.isNotEmpty(getUserPwd()));
    }

    public String getEmail() {
        return Util.getEtString(mEtEmail);
    }

    public String getUserPwd() {
        return Util.getEtString(mEtPwd);
    }
}
