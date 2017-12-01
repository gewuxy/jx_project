package jx.doctor.adapter.meeting;

import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.MeetingVH;
import jx.doctor.model.meet.Meeting;
import jx.doctor.model.meet.Meeting.MeetType;
import jx.doctor.model.meet.Meeting.TMeeting;
import jx.doctor.ui.activity.meeting.MeetingDetailsActivityRouter;
import jx.doctor.ui.activity.meeting.MeetingFolderActivity.ZeroShowType;
import jx.doctor.ui.activity.meeting.MeetingFolderActivityRouter;
import jx.doctor.util.UISetter;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public class MeetingAdapter extends MultiAdapterEx<Meeting, MeetingVH> {

    private boolean mShowUnitNum = true; // 是否显示单位号信息
    private boolean mShowZeroFolder = true; // 会议数是0的文件夹是否显示

    public void hideUnitNum() {
        mShowUnitNum = false;
    }

    public void hideZeroFolder() {
        mShowZeroFolder = false;
    }

    @Override
    protected void refreshView(int position, MeetingVH holder, int itemType) {
        UISetter.meetingHolderSet(holder, getItem(position), mShowUnitNum);
        setOnViewClickListener(position, holder.getItemLayout());
    }

    @Override
    public int getConvertViewResId(int itemType) {
        if (itemType == MeetType.folder) {
            return R.layout.layout_meeting_folder_item;
        } else {
            return R.layout.layout_meeting_item;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getInt(TMeeting.type) == MeetType.folder) {
            return MeetType.folder;
        } else {
            return MeetType.meet;
        }
    }

    @Override
    public int getViewTypeCount() {
        return MeetType.class.getDeclaredFields().length;
    }

    @Override
    protected void onViewClick(int position, View v) {
        Meeting item = getItem(position);
        if (getItemViewType(position) == MeetType.folder) {
            MeetingFolderActivityRouter.create(item.getString(TMeeting.id))
                    .title(item.getString(TMeeting.meetName))
                    .num(item.getInt(TMeeting.meetCount, 0))
                    .showZero(mShowZeroFolder ? ZeroShowType.show : ZeroShowType.hide)
                    .route(getContext());
        } else {
            MeetingDetailsActivityRouter.create(
                    item.getString(TMeeting.id), item.getString(TMeeting.meetName)
            ).route(getContext());
        }
    }

}