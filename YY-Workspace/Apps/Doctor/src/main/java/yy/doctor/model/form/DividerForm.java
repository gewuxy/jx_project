package yy.doctor.model.form;

import android.support.annotation.NonNull;

import lib.ys.ConstantsEx;
import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/7
 */
public class DividerForm extends BaseForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.divider;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_divider;
    }

    @Override
    protected void refresh(FormVH holder) {

        int background = getInt(TForm.background);
        if (background != ConstantsEx.KInvalidValue) {
            holder.getDivider().setBackgroundColor(background);
        }

        int paddingLeft = getInt(TForm.padding_dp_left);
        int paddingRight = getInt(TForm.padding_dp_right);

        if (paddingLeft != ConstantsEx.KInvalidValue && paddingRight != ConstantsEx.KInvalidValue) {
            holder.getDividerLayout().setPadding(paddingLeft, 0, paddingRight, 0);
        }

    }

}
