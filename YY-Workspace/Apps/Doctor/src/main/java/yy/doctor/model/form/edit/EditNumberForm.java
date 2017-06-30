package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;

import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class EditNumberForm extends EditForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.et_number;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit_number;
    }

}
