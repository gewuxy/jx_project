package yy.doctor.model.form.menu;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class FIGroup extends FormItem {

    @NonNull
    @Override
    public int getType() {
        return MenuType.group;
    }

    @Override
    protected void refresh(FormItemVH holder) {
        super.refresh(holder);

        //holder.getTvText().setText();
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_item_menu_group;
    }
}
