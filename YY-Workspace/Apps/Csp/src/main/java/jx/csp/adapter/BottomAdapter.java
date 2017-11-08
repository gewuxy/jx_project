package jx.csp.adapter;

import android.text.SpannableString;

import jx.csp.R;
import jx.csp.adapter.VH.BottomVH;
import lib.ys.adapter.AdapterEx;

/**
 * @auther : GuoXuan
 * @since : 2017/9/1
 */
public class BottomAdapter extends AdapterEx<SpannableString, BottomVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_dialog_bottom_item;
    }

    @Override
    protected void refreshView(int position, BottomVH holder) {
        holder.getTv().setText(getItem(position));
    }
}
