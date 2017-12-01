package lib.jx.test;

import lib.ys.adapter.AdapterEx;
import lib.jx.R;

/**
 * @author yuansui
 */
public class TestAdapter extends AdapterEx<Test, TestVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_test_item;
    }

    @Override
    protected void refreshView(int position, TestVH holder) {
        holder.getBtn().setText(getItem(position).mName);
        if (getItem(position).mListener != null) {
            holder.getBtn().setOnClickListener(getItem(position).mListener);
        }
    }
}
