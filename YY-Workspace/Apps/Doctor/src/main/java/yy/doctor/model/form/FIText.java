package yy.doctor.model.form;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import yy.doctor.R;

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
        return false;
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
