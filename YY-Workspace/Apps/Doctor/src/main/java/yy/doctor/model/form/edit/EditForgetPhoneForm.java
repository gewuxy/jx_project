package yy.doctor.model.form.edit;

import lib.ys.ConstantsEx;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/4
 */

public class EditForgetPhoneForm extends EditPhoneNumberForm {

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit_forget_phone_number;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);
    }
}
