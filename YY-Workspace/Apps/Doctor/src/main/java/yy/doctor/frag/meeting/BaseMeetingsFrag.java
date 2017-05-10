package yy.doctor.frag.meeting;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.MeetingsAdapter;
import yy.doctor.model.meet.MeetRec;

/**
 * @auther yuansui
 * @since 2017/4/24
 */

abstract public class BaseMeetingsFrag extends BaseSRListFrag<MeetRec, MeetingsAdapter> {

    @Override
    public void initNavBar(NavBar bar) {
    }


    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(1);
    }

    @Override
    public boolean enableRefreshWhenInit() {
        return false;
    }

//    @Override
//    public IListResponse<MeetRec> parseNetworkResponse(int id, String text) throws JSONException {
//        return JsonParser.evs(text, MeetRec.class);
//    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);
        startActivity(MeetingDetailsActivity.class);
    }
}
