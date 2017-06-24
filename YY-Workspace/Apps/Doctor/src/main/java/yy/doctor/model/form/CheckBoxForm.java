package yy.doctor.model.form;

import android.support.annotation.NonNull;

import lib.yy.model.form.BaseForm;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class CheckBoxForm extends BaseForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.profile_checkbox;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_checkbox;
    }

}
