package yy.doctor.model.form.menu;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;

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

    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public int getContentViewResId() {
        return 0;
    }
}
