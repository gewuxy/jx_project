package jx.csp.model.form.text;

import jx.csp.R;
import lib.yy.adapter.VH.FormVH;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class BindForm extends TextForm {
    @Override
    public int getContentViewResId() {
        return R.layout.form_text_bind;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);
        holder.getTvText().setText(getText());
    }
}
