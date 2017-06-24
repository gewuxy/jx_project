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
public class SRGroupListLayout extends BaseSRLoadMoreLayout<FloatingGroupListView> {

    public SRGroupListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected FloatingGroupListView initContentView(Context context, AttributeSet attrs) {
        FloatingGroupListView lv = new FloatingGroupListView(context, attrs);
        lv.setId(R.id.sr_group_list_view);
        return lv;
    }

    @Override
    protected void addLoadMoreFooterView(View footerView) {
        getContentView().addFooterView(footerView);
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
