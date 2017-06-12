package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingsVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.util.UISetter;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingsAdapter extends AdapterEx<Meeting, MeetingsVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_item;
    }

    @Override
    protected void refreshView(int position, MeetingsVH holder) {
        holder.getIvNum()
                .placeHolder(R.mipmap.ic_default_unit_num)
                .url(getItem(position).getString(TMeeting.headimg))
                .renderer(new CircleRenderer())
                .load();
        holder.getTvTitle().setText(getItem(position).getString(TMeeting.meetName));
        holder.getTvSection().setText(getItem(position).getString(TMeeting.meetType));
        holder.getTvNum().setText(getItem(position).getString(TMeeting.organizer));

        long startTime = getItem(position).getLong(TMeeting.startTime);
        long endTime = getItem(position).getLong(TMeeting.endTime);
        UISetter.setDateDuration(holder.getTvDate(), holder.getTvDuration(), startTime, endTime);

        @MeetsState int state = getItem(position).getInt(TMeeting.state);
        UISetter.setMeetState(state, holder.getTvState());
    }
}
