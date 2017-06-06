package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeUtil;
import lib.ys.util.res.ResLoader;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingsVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
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
                .load();
        holder.getTvTitle().setText(getItem(position).getString(TMeeting.meetName));
        holder.getTvSection().setText(getItem(position).getString(TMeeting.meetType));
        holder.getTvNum().setText(getItem(position).getString(TMeeting.organizer));
        holder.getTvTime().setText(TimeUtil.formatMilli(getItem(position).getLong(TMeeting.startTime), "MM月dd日 HH:mm"));
        holder.getTvData().setText("时长:" +
                Util.timeParse(getItem(position).getLong(TMeeting.endTime) -
                        getItem(position).getLong(TMeeting.startTime)));

        setState(position, holder);
    }

    /**
     * 设置会议状态
     *
     * @param position
     * @param holder
     */
    private void setState(int position, MeetingsVH holder) {
        int state = getItem(position).getInt(TMeeting.state);
        String strState = null;
        int resId = 0;
        int colorId = 0;
        switch (state) {
            case MeetsState.not_started: {
                strState = "未开始";
                resId = R.mipmap.meeting_ic_not_started;
                colorId = R.color.text_01b557;
            }
            break;
            case MeetsState.under_way: {
                strState = "进行中";
                resId = R.mipmap.meeting_ic_under_way;
                colorId = R.color.text_e6600e;
            }
            break;
            case MeetsState.retrospect: {
                strState = "精彩回顾";
                resId = R.mipmap.meeting_ic_retrospect;
                colorId = R.color.text_5cb0de;
            }
            break;
        }
        holder.getTvState().setText(strState);
        holder.getTvState().setTextColor(ResLoader.getColor(colorId));
        holder.getIvState().setImageDrawable(ResLoader.getDrawable(resId));
    }

}
