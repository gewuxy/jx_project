package jx.csp.model.form.divider;

import jx.csp.R;
import lib.jx.adapter.VH.FormVH;

/**
 * @auther yuansui
 * @since 2017/7/25
 */

public class DividerMarginForm extends DividerForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_divider_margin;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        holder.getDividerLayout().setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
    }
}
