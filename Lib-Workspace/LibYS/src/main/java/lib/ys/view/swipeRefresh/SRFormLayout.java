package lib.ys.view.swipeRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import lib.ys.R;
import lib.ys.view.swipeRefresh.base.BaseSRLayout;

/**
 * SwipeRefreshFormItemLayout
 *
 * @author yuansui
 */
public class SRFormLayout extends BaseSRLayout {

    public SRFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initContentView(Context context, AttributeSet attrs) {
        return inflate(getContext(), R.layout.layout_form_items, null);
    }

}
