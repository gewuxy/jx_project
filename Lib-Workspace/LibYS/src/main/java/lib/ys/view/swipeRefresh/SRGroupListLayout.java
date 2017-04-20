package lib.ys.view.swipeRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import lib.ys.R;
import lib.ys.view.FloatingGroupListView;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;

/**
 * SwipeRefreshExpendableListView
 *
 * @author yuansui
 */
public class SRGroupListLayout extends BaseSRLoadMoreLayout {

    private FloatingGroupListView mLv;

    public SRGroupListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        mLv = new FloatingGroupListView(context, attrs);
        mLv.setId(R.id.sr_group_list_view);
        return mLv;
    }

    @Override
    protected void addLoadMoreFooterView(View footerView) {
        mLv.addFooterView(footerView);
    }

    @Override
    public void addHeaderView(View v) {
        mLv.addHeaderView(v);
    }
}
