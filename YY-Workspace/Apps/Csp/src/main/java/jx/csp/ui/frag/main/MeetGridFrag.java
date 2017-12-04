package jx.csp.ui.frag.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import java.util.List;

import jx.csp.R;
import jx.csp.adapter.main.MeetGridAdapter;
import jx.csp.contact.MeetContract;
import jx.csp.model.main.Meet;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.presenter.MeetPresenterImpl;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.frag.base.BaseSRRecyclerFrag;

/**
 * 首页九宫格的frag
 *
 * @auther WangLan
 * @since 2017/10/18
 */
public class MeetGridFrag extends BaseSRRecyclerFrag<Meet, MeetGridAdapter> implements
        IMeetOpt,
        MeetContract.V,
        OnAdapterClickListener {

    private OnMeetGridListener mListener;

    private MeetContract.P mPresenter;

    public interface OnMeetGridListener {
        void onMeetRefresh(List<Meet> data);
    }

    @Override
    public void initData() {
        mPresenter = new MeetPresenterImpl(this, getContext());
    }

    @Override
    public void initNavBar(NavBar bar) {
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
        exeNetworkReq(MeetingAPI.meetingList(getOffset(), getLimit()).build());
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
    public void onStopRefresh() {
    }

    @Override
    public void allowEnter() {
        if (mPresenter == null) {
            YSLog.d(TAG, "grid enter p = null");
            return;
        }
        mPresenter.allowEnter();
    }

    @Override
    public void notAllowEnter() {
        if (mPresenter == null) {
            YSLog.d(TAG, "grid noEnter p = null");
            return;
        }
        mPresenter.notAllowEnter();
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (mListener != null) {
            mListener.onMeetRefresh(getData());
        }
    }
}
