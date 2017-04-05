package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.MenuVH;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class MenuAdapter extends AdapterEx<String, MenuVH>{

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_menu_item;
    }

    @Override
    protected void refreshView(int position, MenuVH holder) {

    }
}
