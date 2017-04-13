package yy.doctor.model.form;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class FIContentNoImgTx extends FormItem {

    @NonNull
    @Override
    public int getType() {
        return FormType.content_no_img_tx;
    }

    @Override
    protected void refresh(FormItemVH holder) {
        super.refresh(holder);

    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_item_content_no_img_tx;
    }
}
