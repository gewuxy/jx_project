package jx.doctor.adapter.meeting;

import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.OverviewVH;
import jx.doctor.model.meet.ppt.Course;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @author CaiXiang
 * @since 2018/3/1
 */

public class OverviewAdapter extends RecyclerAdapterEx<Course, OverviewVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_overview_item;
    }

    @Override
    protected void refreshView(int position, OverviewVH holder) {
        holder.getTv().setText(String.valueOf(position + 1));
        holder.getIv()
                .placeHolder(R.drawable.ic_default_overview)
                .url(getItem(position).getString(Course.TCourse.imgUrl))
                .load();
    }
}
