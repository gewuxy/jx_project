package yy.doctor.adapter.meeting;

import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.meet.Meeting.MeetType;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;
import yy.doctor.ui.activity.meeting.MeetingFolderActivityIntent;
import yy.doctor.util.UISetter;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public class MeetingAdapter extends MultiAdapterEx<Meeting, MeetingVH> {

    private boolean mShowUnitNum = true;

    public void hideUnitNum() {
        mShowUnitNum = false;
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
        if(getItem(position).getInt(TMeeting.type) == MeetType.meet) {
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
            MeetingFolderActivityIntent
                    .create(item.getString(TMeeting.meetName), item.getInt(TMeeting.meetCount))
                    .infinityId(item.getString(TMeeting.id))
                    .start(getContext());
        } else {
            MeetingDetailsActivity.nav(getContext(), item.getString(TMeeting.id), item.getString(TMeeting.meetName));
        }
    }

}