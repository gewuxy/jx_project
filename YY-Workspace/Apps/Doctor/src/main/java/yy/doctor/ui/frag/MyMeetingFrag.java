package yy.doctor.ui.frag;

import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.network.NetFactory;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class MyMeetingFrag extends BaseSRListFrag<Meeting, MeetingAdapter> {
    private boolean mFlag;
    @Override
    public void initData() {
        mFlag = true;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public View createHeaderView() {
        if (mFlag) {
             mFlag = false;
            return inflate(R.layout.layout_divider);
        }
        return null;
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collectionMeetings(getOffset(), getLimit()));
       // exeNetworkReq(NetFactory.thomsonAll(getOffset(), getLimit()));
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


    @Override
    public int getLimit() {
        return 500;
    }

    @Override
    public boolean enableInitRefresh() {
        return true;
    }

}
