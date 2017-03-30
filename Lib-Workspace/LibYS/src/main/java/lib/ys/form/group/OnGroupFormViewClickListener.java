package lib.ys.form.group;

import android.view.View;

public interface OnGroupFormViewClickListener {
    /**
     * item里面的单个view点击
     */
    void onItemViewClick(View v, int groupPosition, int childPosition);
}
