package yy.doctor.model.form.divider;

import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/7
 */
public class DividerForm extends BaseForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_divider;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    protected void init(FormVH holder) {
        holder.getConvertView().setEnabled(false);
    }

    @Override
    protected void refresh(FormVH holder) {
        int background = getBgColor();
        if (background >= 0) {
            holder.getDivider().setBackgroundColor(background);
        }

        holder.getDividerLayout().setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
    }

}
