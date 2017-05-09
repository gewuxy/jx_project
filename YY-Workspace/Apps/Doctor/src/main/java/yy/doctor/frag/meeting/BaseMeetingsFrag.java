package yy.doctor.frag.meeting;

import android.view.View;

import org.json.JSONException;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.MeetingsAdapter;
import yy.doctor.model.meet.MeetRec;
import yy.doctor.network.JsonParser;

/**
 * @auther yuansui
 * @since 2017/4/24
 */

abstract public class BaseMeetingsFrag extends BaseSRListFrag<MeetRec> {

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public MultiAdapterEx<MeetRec, ? extends ViewHolderEx> createAdapter() {
        return new MeetingsAdapter();
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(1);
    }

    @Override
    public boolean canAutoRefresh() {
        return false;
    }

    @Override
    public IListResponse<MeetRec> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.evs(text, MeetRec.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
        stopRefresh();
        IListResponse<MeetRec> r = (IListResponse<MeetRec>) result;
        if (r.isSucceed()) {
            addAll(r.getData());
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);
        startActivity(MeetingDetailsActivity.class);
    }
}
