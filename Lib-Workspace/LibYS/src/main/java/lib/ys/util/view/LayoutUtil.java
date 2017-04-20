package lib.ys.util.view;

import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * 获取layout param专用
 */
@SuppressWarnings("deprecation")
public class LayoutUtil {

    public static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    public static RelativeLayout.LayoutParams getRelativeParams(int w, int h) {
        return new RelativeLayout.LayoutParams(w, h);
    }

    public static AbsoluteLayout.LayoutParams getAbsParams(int x, int y) {
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, x, y);
        return params;
    }

    public static AbsoluteLayout.LayoutParams getAbsParams(float x, float y) {
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, (int) x, (int) y);
        return params;
    }

    public static AbsoluteLayout.LayoutParams getAbsParams(int w, int h, int x, int y) {
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(w, h, x, y);
        return params;
    }

    public static AbsoluteLayout.LayoutParams getAbsParams(float w, float h, float x, float y) {
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams((int) w, (int) h, (int) x, (int) y);
        return params;
    }

    public static AbsListView.LayoutParams getAbsListParams(int w, int h) {
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(w, h);
        return params;
    }

    public static LinearLayout.LayoutParams getLinearParams(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    public static FrameLayout.LayoutParams getFrameParams(int w, int h) {
        return new FrameLayout.LayoutParams(w, h);
    }

    public static ScrollView.LayoutParams getScrollViewParams(int w, int h) {
        return new ScrollView.LayoutParams(w, h);
    }

    public static ScrollView.LayoutParams getScrollViewParams(float w, float h) {
        return new ScrollView.LayoutParams((int) w, (int) h);
    }

    public static LayoutParams getViewGroupParams(int w, int h) {
        return new LayoutParams(w, h);
    }
}
