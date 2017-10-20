package jx.csp.ui.activity.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;

import jx.csp.adapter.main.SquareAdapter;
import jx.csp.model.main.Square;
import jx.csp.model.main.Square.TSquare;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRRecyclerFrag;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class MainSquareFrag extends BaseSRRecyclerFrag<Square,SquareAdapter> {

    @Override
    public void initData() {
        for (int i = 0; i < 20; i++) {
            Square item = new Square();
            item.put(TSquare.title,"asda"+i);
            addItem(item);
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();
    }

    @Override
    protected LayoutManager initLayoutManager() {
        return new GridLayoutManager(getContext(),2);
    }

    @Override
    public void setViews() {
        super.setViews();
    }


    @Override
    public void getDataFromNet() {
        exeNetworkReq(MeetingAPI.meetingList().pageNum(getOffset()).pageSize(getLimit()).build());
    }

    @Override
    public void onSwipeRefreshAction() {
    }

    @Override
    public int getOffset() {
        return 1;
    }
}
