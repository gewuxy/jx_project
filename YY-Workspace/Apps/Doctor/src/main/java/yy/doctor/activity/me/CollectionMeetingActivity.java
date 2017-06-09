package yy.doctor.activity.me;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.CollectionMeetingAdapter;
import yy.doctor.model.me.CollectionMeetings;
import yy.doctor.model.me.CollectionMeetings.TCollectionMeetings;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 收藏的会议
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class CollectionMeetingActivity extends BaseSRListActivity<CollectionMeetings, CollectionMeetingAdapter> {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "收藏的会议", this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collectionMeetings(1, 8));
    }

    @Override
    public void onItemClick(View v, int position) {

        CollectionMeetings item = getItem(position);
        MeetingDetailsActivity.nav(this, item.getString(TCollectionMeetings.id));
    }

}
