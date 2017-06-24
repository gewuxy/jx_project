package yy.doctor.activity.me;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
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
public class CollectionMeetingActivity extends BaseSRListActivity<Meeting, MeetingAdapter> {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.collection_meeting, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collectionMeetings(getOffset(), getLimit()));
    }

    @Override
    public void onItemClick(View v, int position) {

        Meeting item = getItem(position);
        MeetingDetailsActivity.nav(this, item.getString(TMeeting.id));
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.collection_meeting_empty);
    }

}
