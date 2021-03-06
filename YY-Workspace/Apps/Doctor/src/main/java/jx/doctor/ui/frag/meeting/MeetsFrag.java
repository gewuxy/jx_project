package jx.doctor.ui.frag.meeting;

import android.os.Bundle;
import android.view.View;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkReq;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.frag.base.BaseSRListFrag;
import jx.doctor.adapter.meeting.MeetingAdapter;
import jx.doctor.model.meet.Meeting;
import jx.doctor.model.meet.Meeting.MeetState;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.util.Util;

/**
 * 会议列表(进行中, 未开始, 已结束)
 *
 * @auther yuansui
 * @since 2017/4/24
 */
@Route
public class MeetsFrag extends BaseSRListFrag<Meeting, MeetingAdapter> {

    @MeetState
    @Arg
    int mState; // 会议状态

    private String mDepart;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().hideZeroFolder();
    }

    @Override
    public void getDataFromNet() {
        NetworkReq r = MeetAPI.meets()
                .state(mState)
                .depart(mDepart)
                .pageNum(getOffset())
                .pageSize(getLimit())
                .build();
        exeNetworkReq(r);
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
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.section_change) {
            mDepart = (String) data;
            if (Util.noNetwork()) {
                setViewState(ViewState.error);
            }else {
                refresh();
            }
        }
    }
}
