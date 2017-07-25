package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;

import lib.ys.ConstantsEx;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @auther WangLan
 * @since 2017/7/10
 */

public class EditEmailForm extends EditForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.et_email;
    }

    @Override
    public int getContentViewResId() {
        int layout = getInt(TForm.layout);
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_email;
    }

}
