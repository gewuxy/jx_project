package lib.ys.view.swipeRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import lib.ys.R;
import lib.ys.view.swipeRefresh.base.BaseSRLayout;

/**
 * SwipeRefreshGridView
 * TODO: 未完成, gridView本身不能添加footer
 *
 * @author yuansui
 */
public class SRGridListLayout extends BaseSRLayout {

    private GridView mGv;

    public SRGridListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        mGv = new GridView(context, attrs);
        mGv.setId(R.id.sr_grid_view);
        return mGv;
    }
}
