package lib.ys.view.swipeRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import lib.ys.R;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;

/**
 * SwipeRefreshListView
 *
 * @author yuansui
 */
public class SRListLayout extends BaseSRLoadMoreLayout {

    private ListView mLv;

    public SRListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        mLv = new ListView(context, attrs);
        mLv.setId(R.id.sr_list_view);
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

    public void setSelection(int position) {
        mLv.setSelection(position);
    }
}
