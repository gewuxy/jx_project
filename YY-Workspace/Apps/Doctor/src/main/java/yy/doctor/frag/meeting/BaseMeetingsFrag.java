package yy.doctor.frag.meeting;

import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.MeetingsAdapter;

/**
 * @auther yuansui
 * @since 2017/4/24
 */

abstract public class BaseMeetingsFrag extends BaseSRListFrag<String> {

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new MeetingsAdapter();
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(12);
    }

    @Override
    public boolean canAutoRefresh() {
        return false;
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);
        startActivity(MeetingDetailsActivity.class);
    }
}
