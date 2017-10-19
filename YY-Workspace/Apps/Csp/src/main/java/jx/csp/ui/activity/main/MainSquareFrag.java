package jx.csp.ui.activity.main;

import android.support.v7.widget.GridLayoutManager;

import jx.csp.R;
import jx.csp.adapter.main.SquareAdapter;
import jx.csp.model.main.Square;
import jx.csp.model.main.Square.TSquare;
import lib.ys.ui.other.NavBar;
import lib.ys.view.recycler.WrapRecyclerView;
import lib.yy.ui.frag.base.BaseRecyclerFrag;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class MainSquareFrag extends BaseRecyclerFrag<Square,SquareAdapter> {
    private WrapRecyclerView mRecyclerView;

    @Override
    public void initData() {
        for (int i = 0; i < 20; i++) {
            Square item = new Square();
            item.put(TSquare.title,"asda"+i);
            addItem(item);
        }
    }

    @Override
    public void findViews() {
        super.findViews();
        mRecyclerView = findView(R.id.recycler_view);
    }

    @Override
    public void setViews() {
        super.setViews();
        GridLayoutManager mgr = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(mgr);
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void getDataFromNet() {

    }
}
