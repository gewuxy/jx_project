package yy.doctor.frag.meeting;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.Notifier.NotifyType;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;

/**
 * @auther yuansui
 * @since 2017/4/24
 */

abstract public class BaseMeetingsFrag extends BaseSRListFrag<Meeting, MeetingAdapter> {
    protected String mDepart;

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(1);
    }

    @Override
    public void onItemClick(View v, int position) {
        String meetId = getItem(position).getString(TMeeting.id);
        MeetingDetailsActivity.nav(getContext(), meetId);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.section_change) {
            mDepart = (String) data;
            refresh();
        }
    }

    @Override
    protected String getEmptyText() {
        return "暂时没有相关会议";
    }
}
