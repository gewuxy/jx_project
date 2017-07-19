package yy.doctor.model.form.text;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class ContentForm extends BaseForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.content;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        setIvIfValid(holder.getIv(), getInt(TForm.drawable));

        holder.getTvText().setText(getString(TForm.text));
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_content;
    }

}
