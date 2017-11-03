package jx.csp.ui.frag.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import jx.csp.R;
import jx.csp.adapter.main.MeetGridAdapter;
import jx.csp.contact.MeetContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.presenter.MeetPresenterImpl;
import jx.csp.ui.activity.main.MainActivity.OnMeetGridListener;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRRecyclerFrag;

/**
 * 首页九宫格的frag
 *
 * @auther WangLan
 * @since 2017/10/18
 */
public class MeetGridFrag extends BaseSRRecyclerFrag<Meet, MeetGridAdapter>
        implements IMeetOpt,
        MeetContract.V,
        OnAdapterClickListener {

    private OnMeetGridListener mListener;

    private MeetContract.P mPresenter;


    @Override
    public void initData() {
        mPresenter = new MeetPresenterImpl(this, getContext());
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_main_grid;
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener(this);
    }

    @Override
    protected LayoutManager initLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(MeetingAPI.meetingList().pageNum(getOffset()).pageSize(getLimit()).build());
    }

    @Override
    public void onNetRefreshSuccess() {
        super.onNetRefreshSuccess();

        if (mListener != null) {
            mListener.onMeetRefresh(getData());
        }
    }

    @Override
    public void onAdapterClick(int position, View v) {
        Meet item = getItem(position);

        switch (v.getId()) {
            case R.id.main_meet_layout: {
                mPresenter.onMeetClick(item);
            }
            break;
            case R.id.main_meet_iv_share: {
                mPresenter.onShareClick(item);
            }
            break;
            case R.id.main_meet_iv_live: {
                mPresenter.onLiveClick(item);
            }
            break;
        }
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_main_empty_footer);
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.ready);
    }

    public void setListener(OnMeetGridListener listener) {
        mListener = listener;
    }

    @Override
    public void setPosition(int position) {
        setSelection(position);
    }

    @Override
    public int getPosition() {
        RecyclerView rv = getScrollableView();
        GridLayoutManager l = (GridLayoutManager) rv.getLayoutManager();
        return l.findFirstVisibleItemPosition();
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.delete_meeting) {
            String id= (String) data;
                for (Meet meet:getData()) {
                    if (id == meet.getString(TMeet.id)) {
                        getData().remove(meet);
                        invalidate();
                        Notifier.inst().notify(NotifyType.vp_frag_delete_meeting);
                        break;
                    }
            }
        }else if (type == NotifyType.copy_duplicate) {
            String id = (String) data;
            YSLog.d("ooooooooooo",id);
            for (Meet m:getData()){
                if (id == m.getString(TMeet.id)) {
                    getData().add(m);
                    invalidate();
                    Notifier.inst().notify(NotifyType.vp_frag_copy_duplicate);
                    break;
                }
            }
        }
    }

    @Override
    public void onStopRefresh() {

    }
}
