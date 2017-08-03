package yy.doctor.ui.frag;

import android.view.View;

import org.json.JSONException;

import java.util.List;

import lib.network.model.interfaces.IListResult;
import lib.ys.ui.other.NavBar;
import lib.yy.network.ListResult;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.IMeet;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class MyMeetingFrag extends BaseSRListFrag<IMeet, MeetingAdapter> {

    private boolean mFlag;

    private int mType = 0; // type为0，表示会议
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
        exeNetworkReq(NetFactory.collection(getOffset(), getLimit(),mType));
    }

    @Override
    public IListResult<IMeet> parseNetworkResponse(int id, String text) throws JSONException {
        ListResult r = JsonParser.evs(text, Meeting.class);
        return r;
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
            List<IMeet> list = getData();
            for (IMeet meeting : list) {
                if (meetingId.equals(((Meeting) meeting).getString(TMeeting.id))) {
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
