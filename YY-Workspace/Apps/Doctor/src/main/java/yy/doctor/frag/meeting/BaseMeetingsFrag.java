package yy.doctor.frag.meeting;

import android.view.View;

import org.json.JSONException;

import lib.network.model.err.NetError;
import lib.network.model.interfaces.IListResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.meeting.MeetingsAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.network.JsonParser;

/**
 * @auther yuansui
 * @since 2017/4/24
 */

abstract public class BaseMeetingsFrag extends BaseSRListFrag<Meeting, MeetingsAdapter> {

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

    @Override
    public IListResult<Meeting> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.evs(text, Meeting.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
        setViewState(ViewState.normal);
        IListResult<Meeting> r = (IListResult<Meeting>) result;
        if (r.isSucceed()) {
            addAll(r.getData());
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onItemClick(View v, int position) {
        String meetId = getItem(position).getString(TMeeting.id);
        MeetingDetailsActivity.nav(getContext(), meetId);
    }
}
