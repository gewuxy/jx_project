package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.SectionFilterVH;
import yy.doctor.model.meet.MeetingDepartment;
import yy.doctor.model.meet.MeetingDepartment.TMeetingDepartment;

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
        holder.getImageView().placeHolder(R.mipmap.ic_section_default)
                .url(item.getString(TMeetingDepartment.icon))
                .load();
        holder.getTvName().setText(item.getString(TMeetingDepartment.name));
        holder.getTvNumber().setText(item.getString(TMeetingDepartment.count));
    }


}
