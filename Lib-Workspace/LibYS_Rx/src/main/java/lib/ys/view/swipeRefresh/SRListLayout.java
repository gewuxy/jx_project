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
public class SRListLayout extends BaseSRLoadMoreLayout<ListView> {

    public SRListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ListView initContentView(Context context, AttributeSet attrs) {
        ListView lv = new ListView(context, attrs);
        lv.setId(R.id.sr_list_view);
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

    public void setSelection(int position) {
        getContentView().setSelection(position);
    }
}
