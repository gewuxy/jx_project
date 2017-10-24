package jx.csp.ui.activity.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;

import jx.csp.adapter.main.SquareAdapter;
import jx.csp.model.main.Square;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.main.MainActivity.OnSquareRefreshListener;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRRecyclerFrag;

/**
 * @auther WangLan
 * @since 2017/10/18
 */
public class MainSquareFrag extends BaseSRRecyclerFrag<Square, SquareAdapter> {

    private OnSquareRefreshListener mListener;

    public void setListener(OnSquareRefreshListener listener) {
        mListener=listener;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
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
            mListener.onSquareRefresh(getData());
        }
    }

    @Override
    public void onSwipeRefreshAction() {
    }

    @Override
    public int getOffset() {
        return 1;
    }
}
