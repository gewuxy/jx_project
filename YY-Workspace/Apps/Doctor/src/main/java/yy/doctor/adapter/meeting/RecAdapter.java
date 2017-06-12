package yy.doctor.adapter.meeting;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import yy.doctor.Constants;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.RecVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.search.IRec;
import yy.doctor.model.search.IRec.RecType;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.util.UISetter;

/**
 * @auther : GuoXuan
 * @since : 2017/6/8
 */

public class RecAdapter extends MultiAdapterEx<IRec, RecVH> {
    @Override
    protected void refreshView(int position, RecVH holder, int itemType) {
        switch (itemType) {
            // 搜索会议
            case RecType.meeting: {
                Meeting item = (Meeting) getItem(position);

                holder.getIvMeetUN()
                        .placeHolder(R.mipmap.ic_default_unit_num)
                        .url(item.getString(TMeeting.headimg))
                        .renderer(new CircleRenderer())
                        .load();
                holder.getTvMeetTitle().setText(item.getString(TMeeting.meetName));
                holder.getTvMeetSection().setText(item.getString(TMeeting.meetType));
                holder.getTvMeetUN().setText(item.getString(TMeeting.organizer));

                long startTime = item.getLong(TMeeting.startTime);
                long endTime = item.getLong(TMeeting.endTime);
                UISetter.setDateDuration(holder.getTvMeetDate(), holder.getTvMeetDuration(), startTime, endTime);

                @Constants.MeetsState int state = item.getInt(TMeeting.state);
                UISetter.setMeetState(state, holder.getTvMeetState());
            }
            break;
            // 搜索单位号
            case RecType.unit_num: {
                UnitNum unitNum = (UnitNum) getItem(position);
                holder.getIvUnitNumUN().placeHolder(R.mipmap.ic_default_epc)
                        .renderer(new CircleRenderer())
                        .url(unitNum.getString(UnitNum.TUnitNum.headimg))
                        .load();
                holder.getTvUnitNumUN().setText(unitNum.getString(UnitNum.TUnitNum.nickname));
            }
            break;
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        switch (itemType) {
            case RecType.meeting:
                return R.layout.layout_meeting_item;
            case RecType.unit_num:
                return R.layout.layout_unit_num_item;
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return RecType.class.getDeclaredFields().length;
    }
}
