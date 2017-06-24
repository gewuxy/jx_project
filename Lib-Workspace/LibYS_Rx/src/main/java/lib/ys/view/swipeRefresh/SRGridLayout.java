package lib.ys.view.swipeRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import lib.ys.R;
import lib.ys.view.GridViewEx;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;

/**
 * SwipeRefreshGridView
 *
 * @author yuansui
 */
public class SRGridLayout extends BaseSRLoadMoreLayout<GridViewEx> {

    public SRGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected GridViewEx initContentView(Context context, AttributeSet attrs) {
        GridViewEx gv = new GridViewEx(context, attrs);
        gv.setId(R.id.sr_grid_view);
        return gv;
    }

    @Override
    protected void addLoadMoreFooterView(View v) {
        getContentView().addFooterView(v);
    }

    @Override
    public void addHeaderView(View v) {
        getContentView().addHeaderView(v);
    }

    @Override
    public void removeHeaderView(View v) {
        getContentView().removeHeaderView(v);
    }
}
