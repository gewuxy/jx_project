package jx.doctor.model.form;

import lib.yy.model.form.BaseForm;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class CheckBoxForm extends BaseForm {

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_checkbox;
    }

}
