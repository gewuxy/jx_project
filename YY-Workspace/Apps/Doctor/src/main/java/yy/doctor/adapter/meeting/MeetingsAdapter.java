package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.util.TimeUtil;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingsVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

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
        holder.getTvTime().setText(TimeUtil.formatMilli(getItem(position).getLong(TMeeting.startTime), "MM月dd日 HH:mm"));
        holder.getTvData().setText("时长:" +
                Util.timeParse(getItem(position).getLong(TMeeting.endTime) -
                        getItem(position).getLong(TMeeting.startTime)));

        @MeetsState int state = getItem(position).getInt(TMeeting.state);
        UISetter.setMeetState(state, holder.getTvState());
    }
}
