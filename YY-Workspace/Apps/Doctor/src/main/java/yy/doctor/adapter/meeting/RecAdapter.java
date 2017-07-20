package yy.doctor.adapter.meeting;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import yy.doctor.R;
import yy.doctor.adapter.VH.me.UnitNumVH;
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.adapter.VH.meeting.RecVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.search.IRec;
import yy.doctor.model.search.IRec.RecType;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;
import yy.doctor.util.UISetter;

/**
 * 搜索
 *
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
                MeetingVH meetVH = holder.getMeetingVH();

                meetVH.getIvUnitNum()
                        .placeHolder(R.mipmap.ic_default_unit_num)
                        .url(item.getString(TMeeting.headimg))
                        .renderer(new CircleRenderer())
                        .load();
                meetVH.getTvTitle().setText(item.getString(TMeeting.meetName));
                meetVH.getTvSection().setText(item.getString(TMeeting.meetType));
                meetVH.getTvUnitNum().setText(item.getString(TMeeting.organizer));

                long startTime = item.getLong(TMeeting.startTime);
                long endTime = item.getLong(TMeeting.endTime);
                UISetter.setDateDuration(meetVH.getTvDate(), meetVH.getTvDuration(), startTime, endTime);

                @MeetState int state = item.getInt(TMeeting.state);
                UISetter.setMeetState(state, meetVH.getTvState());
            }
            break;
            // 搜索单位号
            case RecType.unit_num: {
                UnitNum unitNum = (UnitNum) getItem(position);
                UnitNumVH unitNumVH = holder.getUnitNumVH();

                unitNumVH.getIvChild().placeHolder(R.mipmap.ic_default_epc)
                        .renderer(new CircleRenderer())
                        .url(unitNum.getString(TUnitNum.headimg))
                        .load();
                unitNumVH.getTvChild().setText(unitNum.getString(TUnitNum.nickname));
            }
            break;

            case RecType.more: {
                if (getItemViewType(getLastItemPosition()) == RecType.unit_num) {
                    holder.getTvMore().setText("查看更多单位号");
                } else {
                    holder.getTvMore().setText("查看更多会议");
                }
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
            case RecType.hot:
                return R.layout.layout_reach_hot;
            case RecType.margin:
                return R.layout.layout_reach_margin;
            case RecType.more:
                return R.layout.layout_reach_more;
            default:
                return R.layout.layout_meeting_item;
        }
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
