package yy.doctor.ui.frag.meeting;

import android.view.View;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkReq;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.network.NetworkAPISetter.MeetAPI;

/**
 * 会议列表(进行中, 未开始, 已结束)
 *
 * @auther yuansui
 * @since 2017/4/24
 */
@Route
public class MeetsFrag extends BaseSRListFrag<Meeting, MeetingAdapter> {

    private String mDepart;

    @MeetState
    @Arg
    int mState; // 会议状态

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
            if (!DeviceUtil.isNetworkEnabled()) {
                setViewState(ViewState.error);
            }else {
                refresh();
            }
        }
    }

  /*  @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            mDepart = (String) mData;
            refresh();
        }
        return true;
    }*/
}
