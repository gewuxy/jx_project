package yy.doctor.adapter.VH.meeting;

import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.RecyclerViewHolderEx;
import yy.doctor.R;

/**
 * 科室列表的数据Bean(在主界面上的)
 *
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
