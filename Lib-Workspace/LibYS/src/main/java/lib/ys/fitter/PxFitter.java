package lib.ys.fitter;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.util.view.LayoutUtil;

/**
 * 专门用于适配分辨率, 根据px来设置的
 *
 * @author yuansui
 */
public class PxFitter {
    public static void fitRelateParams(final View v, final int w, final int h) {
        fitRelateParams(v, w, h, null);
    }

    public static void fitRelateParams(final View v, final int w, final int h, final int[] margins) {
        if (v.getViewTreeObserver().isAlive()) {
            v.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    params.width = w;
                    params.height = h;

                    if (margins != null && margins.length == 4) {
                        params.setMargins(margins[0], margins[1], margins[2], margins[3]);
                    }

                    v.setLayoutParams(params);

                    v.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
    }

    public static void fitLinerParams(final View v, final int w, final int h) {
        fitLinerParams(v, w, h, null);
    }

    public static void fitLinerParams(final View v, final int w, final int h, final int[] margins) {
        if (v.getViewTreeObserver().isAlive()) {
            v.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    LayoutParams params = (LayoutParams) v.getLayoutParams();
                    params.width = w;
                    params.height = h;

                    if (margins != null && margins.length == 4) {
                        params.setMargins(margins[0], margins[1], margins[2], margins[3]);
                    }

                    v.setLayoutParams(params);

                    v.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
    }

    public static void fitAbsParams(final View v, final int w, final int h, final int x, final int y) {
        v.setLayoutParams(LayoutUtil.getAbsParams(w, h, x, y));
    }

    public static void fitTvTextSize(TextView v, int size) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }
}
