package yy.doctor.model.form.edit;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;

/**
 * @author GuoXuan
 * @since 2017/6/8
 */
public class EditForgetPwdForm extends EditRegisterPwdForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit_forget_pwd;
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
