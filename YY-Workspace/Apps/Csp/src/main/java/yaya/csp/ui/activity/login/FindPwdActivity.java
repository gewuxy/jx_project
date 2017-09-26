package yaya.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import yaya.csp.R;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;

/**
 * @auther WangLan
 * @since 2017/9/25
 */

public class FindPwdActivity extends BaseLoginActivity {

    @IntDef({
            RelatedId.email,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.input_register_email)
                .layout(R.layout.form_edit_email))
                .drawable(R.drawable.login_ic_email);
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.find_pwd);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.send_email);
    }

    @Override
    protected void toSet() {

    }

    @Override
    protected int getFooterResId() {
        return 0;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
