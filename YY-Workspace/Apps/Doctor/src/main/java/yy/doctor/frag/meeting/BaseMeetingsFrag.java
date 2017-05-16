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
import yy.doctor.adapter.MeetingsAdapter;
import yy.doctor.model.meet.MeetRec;
import yy.doctor.model.meet.MeetRec.TMeetRec;
import yy.doctor.network.JsonParser;

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

    @Override
    public IListResult<MeetRec> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.evs(text, MeetRec.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
        setViewState(ViewState.normal);
        IListResult<MeetRec> r = (IListResult<MeetRec>) result;
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
        super.onItemClick(v, position);
        String meetId = getItem(position).getString(TMeetRec.id);
        MeetingDetailsActivity.nav(getContext(), meetId);
    }
}
