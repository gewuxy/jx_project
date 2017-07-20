package yy.doctor.model.form;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/11
 */

public class RegisterDividerForm extends BaseForm {
    @NonNull
    @Override
    public int getType() {
        return FormType.register_divider;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_register_divider;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);
    }
}
