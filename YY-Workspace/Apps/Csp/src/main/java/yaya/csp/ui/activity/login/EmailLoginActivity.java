package yaya.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import yaya.csp.R;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;

/**
 * @auther WangLan
 * @since 2017/9/22
 */

public class EmailLoginActivity extends BaseLoginActivity {

    @IntDef({
            RelatedId.email,
            RelatedId.pwd,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
        int pwd = 1;
    }

    private EditText mEtEmail;

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.email_address)
                .layout(R.layout.form_edit_email))
                .drawable(R.drawable.login_ic_email);
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et_pwd))
                .related(RelatedId.pwd)
                .hint(R.string.input_pwd)
                .drawable(R.drawable.login_ic_pwd);
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.email_login);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.confirm_login);
    }

    @Override
    protected void toSet() {

    }

    @Override
    protected int getFooterResId() {
        return R.layout.layout_email_login_footer;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
