package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;

import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class FIEditRegister extends FIEdit {

    @NonNull
    @Override
    public int getType() {
        return FormType.et_register;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_item_edit_register;
    }

}
