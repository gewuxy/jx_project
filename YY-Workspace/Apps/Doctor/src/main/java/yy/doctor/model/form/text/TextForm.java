package yy.doctor.model.form.text;

import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class TextForm extends BaseForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_text;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        setIvIfValid(holder.getIv(), getDrawable());

        holder.getTvText().setText(getText());
    }

    @Override
    public boolean check() {
        return checkSelector();
    }

}
