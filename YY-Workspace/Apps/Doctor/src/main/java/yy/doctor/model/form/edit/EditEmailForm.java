package yy.doctor.model.form.edit;

import lib.ys.ConstantsEx;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/10
 */

public class EditEmailForm extends EditForm {

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_email;
    }

}
