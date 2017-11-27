package jx.csp.ui.activity.me.bind;

import android.os.Bundle;
import android.text.Editable;

import io.reactivex.annotations.NonNull;
import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.model.form.Form;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;

/**
 * 认证邮箱
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class BindEmailActivity extends BaseSetActivity {

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et)
                .related(RelatedId.bind_email)
                .hint(R.string.setting_input_email_address)
                .textWatcher(this)
                .layout(R.layout.form_edit_bind_email));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd)
                .hint(R.string.input_login_pwd)
                .textWatcher(this)
                .drawable(R.drawable.login_selector_visible));
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
        mPresenter.checkPwd(getPwd());
        mPresenter.confirmBindAccount(RelatedId.bind_email, getEmail(), getPwd());
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(getEmail()) && TextUtil.isNotEmpty(getPwd()));
    }

    @NonNull
    private String getEmail(){
        return getRelatedItem(RelatedId.bind_email).getVal();
    }

    @NonNull
    private String getPwd(){
        return getRelatedItem(RelatedId.pwd).getVal();
    }

}
