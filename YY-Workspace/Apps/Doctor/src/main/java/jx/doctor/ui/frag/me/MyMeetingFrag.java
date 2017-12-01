package jx.doctor.ui.frag.me;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.frag.base.BaseSRListFrag;
import jx.doctor.R;
import jx.doctor.adapter.meeting.MeetingAdapter;
import jx.doctor.model.meet.Meeting;
import jx.doctor.model.meet.Meeting.TMeeting;
import jx.doctor.network.NetworkApiDescriptor.CollectionAPI;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class MyMeetingFrag extends BaseSRListFrag<Meeting, MeetingAdapter> {

    @DataType
    private int mType = DataType.meeting;

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_divider);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(CollectionAPI.collection(getOffset(), getLimit(), mType).build());
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.empty_collection_meeting);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //会议取消收藏后，收藏会议列表要删除对应的会议
        if (type == NotifyType.collection_cancel_meeting) {
            String meetingId = (String) data;
            List<Meeting> list = getData();
            for (Meeting meeting : list) {
                if (meetingId.equals(meeting.getString(TMeeting.id))) {
                    getData().remove(meeting);
                    invalidate();
                    break;
                }
            }
        }
    }
}
