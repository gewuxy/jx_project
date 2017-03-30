package lib.ys.adapter.interfaces;

import android.view.View;

public interface OnRecyclerItemClickListener {
    void onItemClick(View v, int position);

    void onItemLongClick(View v, int position);
}