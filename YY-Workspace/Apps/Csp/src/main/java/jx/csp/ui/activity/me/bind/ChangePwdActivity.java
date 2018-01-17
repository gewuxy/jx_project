package jx.csp.ui.activity.me.bind;

import android.text.Editable;

import io.reactivex.annotations.NonNull;
import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.model.form.Form;

/**
 * 修改密码
 *
 * @auther HuoXuYu
 * @since 2017/9/25
 */

public class ChangePwdActivity extends BaseSetActivity {

    private final int KLengthMax = 24; // 密码最大长度
    private final int KLengthMin = 6; // 密码最小长度

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.change_old_pwd)
                .hint(R.string.setting_old_pwd)
                .drawable(R.drawable.pwd_selector_visible)
                .layout(R.layout.form_edit_bind_email_pwd)
                .textWatcher(this)
                .limit(KLengthMax));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.change_new_pwd)
                .hint(R.string.setting_new_pwd)
                .drawable(R.drawable.pwd_selector_visible)
                .layout(R.layout.form_edit_bind_email_pwd)
                .textWatcher(this)
                .limit(KLengthMax));
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.setting_change_pwd);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.setting_confirm_change);
    }

    @Override
    protected void doSet() {
        mPresenter.checkPwd(getNewPwd());
        mPresenter.modifyPwd(RelatedId.change_old_pwd, getOldPwd(), getNewPwd());
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(checkPwd(getOldPwd()) && checkPwd(getNewPwd()));
    }

    @NonNull
    private String getOldPwd() {
        return getRelatedItem(RelatedId.change_old_pwd).getVal();
    }

    @NonNull
    private String getNewPwd() {
        return getRelatedItem(RelatedId.change_new_pwd).getVal();
    }

}
