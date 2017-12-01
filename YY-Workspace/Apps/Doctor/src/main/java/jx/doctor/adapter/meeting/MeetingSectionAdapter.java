package jx.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.SectionFilterVH;
import jx.doctor.model.meet.MeetingDepartment;
import jx.doctor.model.meet.MeetingDepartment.TMeetingDepartment;

/**
 * @auther WangLan
 * @since 2017/7/28
 */

public class MeetingSectionAdapter extends AdapterEx<MeetingDepartment, SectionFilterVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_sectionfilter_item;
    }

    @Override
    protected void refreshView(int position, SectionFilterVH holder) {
        if (position == getLastItemPosition()) {
            goneView(holder.getDivider());
        }
        MeetingDepartment item = getItem(position);
        holder.getImageView().placeHolder(R.drawable.ic_section_default)
                .url(item.getString(TMeetingDepartment.icon))
                .load();
        holder.getTvName().setText(item.getString(TMeetingDepartment.name));
        holder.getTvNumber().setText(item.getString(TMeetingDepartment.count));
    }


}
