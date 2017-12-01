package jx.doctor.model.form;

import lib.jx.adapter.VH.FormVH;
import lib.jx.model.form.BaseForm;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class ToggleButtonForm extends BaseForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_toggle_button;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        holder.getTbSwitcher().setToggleState(true);
    }

    @Override
    public boolean check() {
        return false;
    }

}
