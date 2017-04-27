package lib.ys.adapter.interfaces;

import android.view.View;

abstract public class OnRecyclerItemClickListener {
    /**
     * item 点击事件
     *
     * @param v
     * @param position
     */
    abstract public void onItemClick(View v, int position);

    /**
     * item 长按事件, 可能不需要
     *
     * @param v
     * @param position
     */
    public void onItemLongClick(View v, int position) {
    }
}