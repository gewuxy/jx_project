package yy.doctor.adapter.meeting;

import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.MeetType;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivityRouter;
import yy.doctor.ui.activity.meeting.MeetingFolderActivity.ZeroShowType;
import yy.doctor.ui.activity.meeting.MeetingFolderActivityRouter;
import yy.doctor.util.UISetter;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public class MeetingAdapter extends MultiAdapterEx<Meeting, MeetingVH> {

    private boolean mShowUnitNum = true;
    private boolean mShowZeroFolder = true;

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
        if (getItem(position).getInt(TMeeting.type) == MeetType.meet) {
            return MeetType.meet;
        }
        return MeetType.folder;
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