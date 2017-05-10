package lib.ys.network.image.renderer;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;

import static android.R.attr.width;

/**
 * 圆形渲染器
 */
public class CircleRenderer implements Renderer {

    @ColorInt
    private int mBorderColor = Color.TRANSPARENT;

    private float mBorderWidth;

    public CircleRenderer() {
    }

    public CircleRenderer(@ColorInt int color, @FloatRange(from = 0) float width) {
        mBorderWidth = width;
        mBorderColor = color;
    }

    public void setBorderColor(@ColorInt int color) {
        mBorderWidth = width;
    }

    public void setBorderWidth(@FloatRange(from = 0) float width) {
        mBorderWidth = width;
    }

    @ColorInt
    public int getBorderColor() {
        return mBorderColor;
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CircleRenderer) {
            CircleRenderer r = (CircleRenderer) o;
            if (r.getBorderColor() == mBorderColor
                    && r.getBorderWidth() == mBorderWidth) {
                return true;
            }
            return false;
        } else {
            return super.equals(o);
        }
    }
}
