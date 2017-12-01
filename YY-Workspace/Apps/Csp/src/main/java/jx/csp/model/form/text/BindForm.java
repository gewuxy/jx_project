package jx.csp.model.form.text;

import jx.csp.R;
import lib.jx.adapter.VH.FormVH;

/**
 * @author HuoXuYu
 * @since 2017/9/25
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
