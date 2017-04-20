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
public class SRRecyclerLayout extends BaseSRLoadMoreLayout {

    private WrapRecyclerView mRv;

    public SRRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        mRv = new WrapRecyclerView(context, attrs);
        mRv.setId(R.id.sr_recycler_view);
        return mRv;
    }

    @Override
    protected void addLoadMoreFooterView(View footerView) {
        mRv.addFooterView(footerView);
    }

    @Override
    public void addHeaderView(View v) {
        mRv.addHeaderView(v);
    }
}
