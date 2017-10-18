package yy.doctor.ui.frag.me;

import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.network.NetworkApiDescriptor.CollectionAPI;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class MyMeetingFrag extends BaseSRListFrag<Meeting, MeetingAdapter> {

    @DataType
    private int mType = DataType.meeting;

    @Override
    public void initData() {
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
