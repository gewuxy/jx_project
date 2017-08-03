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

public class MeetingDepartmentAdapter extends AdapterEx<MeetingDepartment, SectionFilterVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_sectionfilter_item;
    }

    @Override
    protected void refreshView(int position, SectionFilterVH holder) {
        if (position == 0) {
            goneView(holder.getDivider());
        }
//        holder.getImageView().setImageResource(getItem(position).getInt(TSectionFilter.bitmap));
       holder.getName().setText(getItem(position).getString(TMeetingDepartment.name));
       holder.getNumber().setText(getItem(position).getString(TMeetingDepartment.count));
    }


}
