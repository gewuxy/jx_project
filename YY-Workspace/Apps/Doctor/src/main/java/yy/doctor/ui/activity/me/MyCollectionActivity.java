package yy.doctor.ui.activity.me;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 收藏的会议
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class MyCollectionActivity extends BaseSRListActivity<Meeting, MeetingAdapter> {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.my_collection, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collectionMeetings(getOffset(), getLimit()));
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.collection_meeting_empty);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //会议取消收藏后，收藏会议列表要删除对应的会议
        if (type == NotifyType.cancel_collection_meeting) {
            String meetingId = (String) data;
            List<Meeting> list = getData();
            for (Meeting meeting : list) {
                if (meetingId.equals(meeting.getString(TMeeting.id))) {
                    getData().remove(meeting);
                    invalidate();
                    return;
                }
            }
        }

    }
}
