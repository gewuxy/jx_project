package yy.doctor.model.form.text;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class FIText extends FormItem {

    @NonNull
    @Override
    public int getType() {
        return FormType.text;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_item_text;
    }

    @Override
    protected void refresh(FormItemVH holder) {
        super.refresh(holder);

        setTextIfExist(holder.getTvText(), getString(TFormElem.text));
    }
}
