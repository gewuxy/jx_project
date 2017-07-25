package yy.doctor.adapter.meeting;

import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.meet.Meeting.FileType;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;
import yy.doctor.ui.activity.meeting.MeetingFolderActivity;
import yy.doctor.util.UISetter;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public class MeetingAdapter extends MultiAdapterEx<Meeting, MeetingVH> {

    private boolean mHideUnitNum;

    public void hideUnitNum() {
        mHideUnitNum = true;
    }

    @Override
    protected void refreshView(int position, MeetingVH holder, int itemType) {
        UISetter.meetingHolderSet(holder, getItem(position), mHideUnitNum);
        setOnViewClickListener(position, holder.getItemLayout());
    }

    @Override
    public int getConvertViewResId(int itemType) {
        if (itemType == FileType.folder) {
            return R.layout.layout_meeting_folder_item;
        } else {
            return R.layout.layout_meeting_item;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 5) {
            return FileType.folder;
        } else {
            return FileType.file;
        }
    }

    @Override
    public int getViewTypeCount() {
        return FileType.class.getDeclaredFields().length;
    }

    @Override
    protected void onViewClick(int position, View v) {
        Meeting item = getItem(position);
        String title = item.getString(TMeeting.meetName);
        if (getItemViewType(position) == FileType.folder) {
            MeetingFolderActivity.nav(getContext(), title);
        } else {
            MeetingDetailsActivity.nav(getContext(), item.getString(TMeeting.id), title);
        }
    }
}