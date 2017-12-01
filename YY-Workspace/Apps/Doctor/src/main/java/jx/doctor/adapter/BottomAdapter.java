package jx.doctor.adapter;

import android.text.SpannableString;

import lib.ys.adapter.AdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.BottomVH;

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
