package yy.doctor.model.form.menu;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class FIChild extends FormItem {

    @NonNull
    @Override
    public int getType() {
        return MenuType.child;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_item_menu_child;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    protected void refresh(FormItemVH holder) {
        holder.getTv().setText(getString(TFormElem.name));
        holder.getIv().setImageResource(getInt(TFormElem.drawable));
    }
}
