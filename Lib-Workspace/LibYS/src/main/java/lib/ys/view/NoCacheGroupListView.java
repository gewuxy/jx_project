package lib.ys.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class NoCacheGroupListView extends ExpandableListView {

    public NoCacheGroupListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
