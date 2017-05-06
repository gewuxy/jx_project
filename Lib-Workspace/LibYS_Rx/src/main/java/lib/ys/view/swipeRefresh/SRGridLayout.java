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
public class SRGridLayout extends BaseSRLoadMoreLayout {

    private GridViewEx mGv;

    public SRGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        mGv = new GridViewEx(context, attrs);
        mGv.setId(R.id.sr_grid_view);
        return mGv;
    }

    @Override
    protected void addLoadMoreFooterView(View v) {
        mGv.addFooterView(v);
    }

    @Override
    public void addHeaderView(View v) {
        mGv.addHeaderView(v);
    }
}
