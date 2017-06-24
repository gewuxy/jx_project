package lib.ys.view.swipeRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import lib.ys.R;
import lib.ys.view.recycler.WrapRecyclerView;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;

/**
 * @author yuansui
 */
public class SRRecyclerLayout extends BaseSRLoadMoreLayout<WrapRecyclerView> {

    public SRRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected WrapRecyclerView initContentView(Context context, AttributeSet attrs) {
        WrapRecyclerView rv = new WrapRecyclerView(context, attrs);
        rv.setId(R.id.sr_recycler_view);
        return rv;
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
        // FIXME: 没有实现
    }
}
