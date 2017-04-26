package yy.doctor.adapter.VH;

import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.recycler.RecyclerViewHolderEx;
import yy.doctor.R;

/**
 * @auther yuansui
 * @since 2017/4/26
 */

public class SectionVH extends RecyclerViewHolderEx {

    public SectionVH(View itemView) {
        super(itemView);
    }

    public TextView getTv() {
        return getView(R.id.meeting_section_item_tv);
    }
}
