package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import lib.ys.fitter.DpFitter;
import lib.ys.network.image.renderer.CircleRenderer;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.util.UISetter;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingAdapter extends AdapterEx<Meeting, MeetingVH> {

    private int mImgSize;

    public MeetingAdapter() {
        mImgSize = DpFitter.dimen(R.dimen.meeting_item_unit_num_size);
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_item;
    }

    @Override
    protected void refreshView(int position, MeetingVH holder) {

        Meeting item = getItem(position);

        holder.getTvTitle().setText(item.getString(TMeeting.meetName));

        @MeetsState int state = item.getInt(TMeeting.state);
        UISetter.setMeetState(state, holder.getTvState());

        holder.getTvSection().setText(item.getString(TMeeting.meetType));

        long startTime = item.getLong(TMeeting.startTime);
        long endTime = item.getLong(TMeeting.endTime);
        UISetter.setDateDuration(holder.getTvDate(), holder.getTvDuration(), startTime, endTime);

        holder.getIvUnitNum()
                .placeHolder(R.mipmap.ic_default_unit_num)
                .resize(mImgSize, mImgSize)
                .url(item.getString(TMeeting.headimg))
                .renderer(new CircleRenderer())
                .load();
        holder.getTvUnitNum().setText(item.getString(TMeeting.organizer));
    }
}
