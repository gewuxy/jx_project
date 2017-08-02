package yy.doctor.ui.frag.meeting;

import android.view.View;

import org.json.JSONException;

import lib.network.model.interfaces.IListResult;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.network.ListResult;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.IMeet;
import yy.doctor.model.meet.Meeting;
import yy.doctor.network.JsonParser;

/**
 * @auther yuansui
 * @since 2017/4/24
 */

abstract public class BaseMeetingsFrag extends BaseSRListFrag<IMeet, MeetingAdapter> {

    protected String mDepart;

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(1);
    }

    @Override
    protected String getEmptyText() {
        return "暂时没有相关会议";
    }

    @Override
    public IListResult<IMeet> parseNetworkResponse(int id, String text) throws JSONException {
        ListResult r = JsonParser.evs(text, Meeting.class);
        return r;
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.section_change) {
            mDepart = (String) data;
            refresh();
        }
    }
}
