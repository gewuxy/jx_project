package lib.ys.util.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.reflect.Field;

import lib.ys.AppEx;
import lib.ys.fitter.DpFitter;
import lib.ys.util.DeviceUtil;
import lib.ys.util.UIUtil;
import lib.ys.util.bmp.BmpUtil;

public class ViewUtil {

    public static View inflateLayout(@LayoutRes int id) {
        return LayoutInflater.from(AppEx.ct()).inflate(id, null);
    }

    public static View inflateSpaceViewDp(int dp) {
        return inflateSpaceViewPx(UIUtil.dpToPx(dp));
    }

    public static View inflateSpaceViewDpFit(int dp) {
        return inflateSpaceViewPx(DpFitter.dp(dp));
    }

    public static View inflateSpaceViewPx(int px) {
        Context context = AppEx.ct();
        RelativeLayout layout = new RelativeLayout(context);
        View v = new View(context);
        layout.addView(v, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, px));
        return layout;
    }

    public static View inflateSpaceView(int w, int h) {
        View v = new View(AppEx.ct());
        v.setLayoutParams(new ViewGroup.LayoutParams(w, h));
        return v;
    }

    /**
     * 加入下划线
     *
     * @param tv
     * @deprecated 有时候不好用, 不推荐
     */
    public static void setTextViewUnderLine(TextView tv) {
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * 恢复初始状态
     *
     * @param tv
     */
    public static void resetTextViewFlag(TextView tv) {
        tv.getPaint().setFlags(0 | Paint.DEV_KERN_TEXT_FLAG);
    }

    /**
     * 限制输入字数
     *
     * @param tv
     * @param max
     */
    public static void limitInputCount(TextView tv, int max) {
        tv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
    }

    /**
     * 关闭view层级的硬件加速
     * 部分2D方法在应用开启硬件加速后会失效或崩溃, 需要关闭加速, 如
     * <p>
     * {@link Canvas#clipPath}
     * {@link Canvas#clipRegion}
     * {@link Canvas#drawPicture}
     * {@link Canvas#drawTextOnPath}
     * {@link Canvas#drawVertices}
     * </p>
     * <p>
     * {@link Paint#setLinearText}
     * {@link Paint#setMaskFilter}
     * {@link Paint#setRasterizer}
     * </p>
     *
     * @param v
     * @param compatible 是否使用兼容模式
     */
    public static void disableHardwareAcc(View v, boolean compatible) {
        if (compatible) {
            // 11到17之间 部分机型在clipPath的时候会报UnsupportedOperationException
            int version = DeviceUtil.getSDKVersion();
            if (version < VERSION_CODES.JELLY_BEAN_MR2 // 18
                    && version >= VERSION_CODES.HONEYCOMB // 11
                    ) {
                v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        } else {
            // 强制关闭
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }


    /**
     * 可设置间隔的scroller
     */
    public static class FixedSpeedScroller extends Scroller {
        private int mDuration = 250;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setDuration(int time) {
            mDuration = time;
        }
    }

    public static FixedSpeedScroller setViewPagerScrollDuration(ViewPager viewPager, int duration) {
        FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new DecelerateInterpolator());
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            scroller.setDuration(duration);
            mField.set(viewPager, scroller);
            return scroller;
        } catch (Exception e) {
            return scroller;
        }
    }

    public static boolean isVisible(View v) {
        return v.getVisibility() == View.VISIBLE;
    }

    public static void showView(View v) {
        if (v.getVisibility() != View.VISIBLE) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void goneView(View v) {
        if (v.getVisibility() != View.GONE) {
            v.setVisibility(View.GONE);
        }
    }

    public static void hideView(View v) {
        if (v.getVisibility() != View.INVISIBLE) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public static void setBackground(View v, Drawable background) {
        if (DeviceUtil.getSDKVersion() < VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(background);
        } else {
            v.setBackground(background);
        }
    }

    /**
     * 主动回收iv里面的图片资源
     *
     * @param iv
     */
    public static void recycleIvBmp(ImageView iv) {
        Drawable drawable = iv.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            BmpUtil.recycle(bitmapDrawable.getBitmap());
        }
    }
}
