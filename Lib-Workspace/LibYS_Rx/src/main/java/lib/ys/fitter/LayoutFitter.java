package lib.ys.fitter;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import lib.ys.R;
import lib.ys.model.Screen;
import lib.ys.util.DeviceUtil;

/**
 * 自动获取view的xml里的参数(dp), 根据分辨率的scale来重新设置大小(px)
 *
 * @author yuansui
 */
@SuppressWarnings("deprecation")
public class LayoutFitter {
    public static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    public static final int CENTER_HORIZONTAL = -11;
    public static final int CENTER_VERTICAL = -12;

    private static Set<View> mSetFit = new HashSet<>();

    /**
     * 适配{@link AbsoluteLayout}的属性
     *
     * @param v
     * @param x x坐标, 如果为CENTER_HORIZONTAL, 则动态计算居中
     * @param y y坐标, 如果为CENTER_VERTICAL, 则动态计算居中
     */
    public static void fitAbsByPx(final View v, final int x, final int y) {
        if (v.getViewTreeObserver().isAlive()) {
            v.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) v.getLayoutParams();

                    // 修改宽高
                    params.width = convert(params.width);
                    params.height = convert(params.height);

                    if (x == CENTER_HORIZONTAL) {
                        // 左右居中
                        switch (params.width) {
                            case MATCH_PARENT: {
                                params.x = 0;
                            }
                            break;
                            case WRAP_CONTENT: {
                                params.x = (Screen.getWidth() - v.getWidth()) / 2;
                            }
                            break;
                            default: {
                                // 有固定宽
                                params.x = (Screen.getWidth() - params.width) / 2;
                            }
                            break;
                        }
                    } else {
                        params.x = x;
                    }

                    if (y == CENTER_VERTICAL) {
                        switch (params.height) {
                            case MATCH_PARENT: {
                                // do nothing
                                params.y = 0;
                            }
                            break;
                            case WRAP_CONTENT: {
                                params.y = (Screen.getHeight() - v.getHeight()) / 2;
                            }
                            break;
                            default: {
                                // 固定高
                                params.y = (Screen.getHeight() - params.height) / 2;
                            }
                            break;
                        }
                    } else {
                        params.y = y;
                    }

                    // 修改padding值
                    int paddingLeft = convert(v.getPaddingLeft());
                    int paddingTop = convert(v.getPaddingTop());
                    int paddingRight = convert(v.getPaddingRight());
                    int paddingBottom = convert(v.getPaddingBottom());
                    v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                    v.setLayoutParams(params);

                    v.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static float getTvLineSpacingExtra(TextView tv) {
        if (DeviceUtil.getSDKVersion() >= Build.VERSION_CODES.JELLY_BEAN) {
            return tv.getLineSpacingExtra();
        } else {
            return 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void setTvLineSpacingExtra(TextView tv) {
        if (DeviceUtil.getSDKVersion() >= Build.VERSION_CODES.HONEYCOMB) {
            tv.setLineSpacing(convert(getTvLineSpacingExtra(tv)), 1);
        }
    }

    /**
     * 适配{@link TextView}的内部属性, 不涉及到layoutParams
     *
     * @param tv
     */
    private static void fitTvParams(TextView tv) {
        setTvLineSpacingExtra(tv);

        // 处理drawables
        Drawable[] drawables = tv.getCompoundDrawables();
        boolean drawableSet = false;
        for (Drawable d : drawables) {
            if (d == null) {
                continue;
            }
            drawableSet = true;

            Rect bounds = d.getBounds();
            int left = convert(bounds.left);
            int top = convert(bounds.top);
            int right = convert(bounds.right);
            int bottom = convert(bounds.bottom);
            d.setBounds(left, top, right, bottom);
        }

        if (drawableSet) {
            tv.setCompoundDrawables(
                    drawables[0],
                    drawables[1],
                    drawables[2],
                    drawables[3]
            );
            tv.setCompoundDrawablePadding(convert(tv.getCompoundDrawablePadding()));
        }

        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, convert(tv.getTextSize()));
    }

    private static void setMarginParams(View v, MarginLayoutParams params) {
        // 修改宽高
        params.width = convertWH(params.width);
        params.height = convertWH(params.height);

        // 修改margin值
        params.leftMargin = convert(params.leftMargin);
        params.rightMargin = convert(params.rightMargin);
        params.topMargin = convert(params.topMargin);
        params.bottomMargin = convert(params.bottomMargin);

        // 修改padding值
        int paddingLeft = convert(v.getPaddingLeft());
        int paddingTop = convert(v.getPaddingTop());
        int paddingRight = convert(v.getPaddingRight());
        int paddingBottom = convert(v.getPaddingBottom());
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    /**
     * 处理宽高专用
     *
     * @param val
     * @return
     */
    private static int convertWH(int val) {
        if (val == 0 || val == MATCH_PARENT || val == WRAP_CONTENT || val == 1) {
            return val;
        }

        return DpFitter.densityPx(val);
    }

    /**
     * 用于处理其他数值
     *
     * @param val
     * @return
     */
    private static int convert(int val) {
        if (val == 0 || val == 1 || val == -1) {
            return val;
        }

        return DpFitter.densityPx(val);
    }

    private static int convert(float value) {
        return convert((int) value);
    }

    private static void fit(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); ++i) {
            View v = vg.getChildAt(i);

            if (mSetFit.contains(v)) {
                continue;
            } else {
                mSetFit.add(v);
            }

            int id = v.getId();
            if (id == R.id.flat_bar || id == R.id.nav_bar_divider) {
                /**
                 * 跳过flatBar, 实际上flatBar的高度就是statusBar的高度, 获取的时候已经是fit后的px, 不可以再多处理一次
                 * 同样跳过titleBar的divider
                 */
                continue;
            }

            if (v instanceof ViewGroup) {
                fit(v);
            } else if (v instanceof TextView) {
                fitTvParams((TextView) v);
            }

            fitParams(v);
        }
    }


    /**
     * 适配父布局为view group的view
     *
     * @param v
     */
    private static void fitParams(View v) {
        if (v.getParent() instanceof RelativeLayout || v.getParent() instanceof LinearLayout) {
            MarginLayoutParams params = (MarginLayoutParams) v.getLayoutParams();
            setMarginParams(v, params);
            v.setLayoutParams(params);
        }
    }

    /**
     * 自动设置所有的适配属性, 适用于以下view:
     * <p>
     * {@link TextView}
     * {@link RelativeLayout}
     * {@link LinearLayout}
     * </p>
     *
     * @param v
     */
    public static void fit(View v) {
        if (v instanceof ViewGroup) {
            fit((ViewGroup) v);
        } else if (v instanceof TextView) {
            fitTvParams((TextView) v);
            fitParams(v);
        }
    }

    public static void clearFitSet() {
        mSetFit.clear();
    }
}
