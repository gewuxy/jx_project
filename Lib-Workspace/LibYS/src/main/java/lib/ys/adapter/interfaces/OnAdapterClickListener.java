package lib.ys.adapter.interfaces;

import android.view.View;

@FunctionalInterface
public interface OnAdapterClickListener {
    void onAdapterClick(int position, View v);
}