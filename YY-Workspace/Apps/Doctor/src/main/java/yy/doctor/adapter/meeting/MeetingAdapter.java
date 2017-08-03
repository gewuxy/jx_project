package yy.doctor.adapter.meeting;

import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingVH;
import yy.doctor.model.meet.MeetFolder;
import yy.doctor.model.meet.MeetFolder.TMeetingFolder;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.IMeet;
import yy.doctor.model.meet.IMeet.MeetType;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;
import yy.doctor.ui.activity.meeting.MeetingFolderActivity;
import yy.doctor.ui.activity.meeting.MeetingFolderActivityIntent;
import yy.doctor.util.UISetter;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */
public class MeetingAdapter extends MultiAdapterEx<IMeet, MeetingVH> {

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
        if (itemType == MeetType.folder) {
            return R.layout.layout_meeting_folder_item;
        } else {
            return R.layout.layout_meeting_item;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getMeetType();
    }

    @Override
    public int getViewTypeCount() {
        return MeetType.class.getDeclaredFields().length;
    }

    @Override
    protected void onViewClick(int position, View v) {
        if (getItemViewType(position) == MeetType.folder) {
            MeetFolder item = (MeetFolder) getItem(position);
            MeetingFolderActivityIntent
                    .create(item.getString(TMeetingFolder.infinityName), item.getInt(TMeetingFolder.meetCount))
                    .infinityId(item.getString(TMeetingFolder.id))
                    .start(getContext());
        } else {
            Meeting item = (Meeting) getItem(position);
            MeetingDetailsActivity.nav(getContext(), item.getString(TMeeting.id), item.getString(TMeeting.meetName));
        }
    }
}