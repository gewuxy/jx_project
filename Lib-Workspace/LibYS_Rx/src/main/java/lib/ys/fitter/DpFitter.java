package lib.ys.fitter;

import android.support.annotation.DimenRes;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.config.FitConfig;
import lib.ys.model.Screen;
import lib.ys.util.res.ResLoader;


/**
 * 专门用于适配分辨率, 根据dp来设置的
 *
 * @author yuansui
 */
public class DpFitter {
    // 基于屏幕分辨率的缩放
    private static float mScaleResolution = ConstantsEx.KInvalidValue;
    private static float mDensity = ConstantsEx.KInvalidValue;

    /**
     * 根据缩放比例将dp转换成px
     *
     * @param dp
     * @return
     */
    public static int dp(float dp) {
        return (int) (dp * FitConfig.getDpBaseScale() * getScale());
    }

    /**
     * 根据比例将dimen.xml里的值转换成px
     *
     * @param dimenResId R.dimen.xxx
     * @return
     */
    public static int dimen(@DimenRes int dimenResId) {
        return (int) (getDimensionDp(dimenResId) * FitConfig.getDpBaseScale() * getScale());
    }

    /**
     * 把经过density缩放的px值转换为scale后的px
     *
     * @param px
     * @return
     */
    public static float densityPx(float px) {
        float dp = px / getDensity(); // 转换为dp
        return dp(dp);
    }

    /**
     * 把经过density缩放的px值转换为scale后的px
     *
     * @param px
     * @return
     */
    public static int densityPx(int px) {
        return (int) densityPx((float) px);
    }

    public static float getDensity() {
        if (mDensity == ConstantsEx.KInvalidValue) {
            mDensity = Screen.getDensity();
        }
        return mDensity;
    }

    public static void relateParams(final View v, final int w, final int h) {
        relateParams(v, w, h, null);
    }

    public static void relateParams(final View v, final int w, final int h, final int[] margins) {
        if (margins != null && margins.length == 4) {
            margins[0] = dp(margins[0]);
            margins[1] = dp(margins[1]);
            margins[2] = dp(margins[2]);
            margins[3] = dp(margins[3]);
        }
        PxFitter.fitRelateParams(v, getRealValue(w), getRealValue(h), margins);
    }

    public static void linerParams(final View v, final int w, final int h) {
        linerParams(v, w, h, null);
    }

    public static void linerParams(final View v, final int w, final int h, final int[] margins) {
        if (margins != null && margins.length == 4) {
            margins[0] = dp(margins[0]);
            margins[1] = dp(margins[1]);
            margins[2] = dp(margins[2]);
            margins[3] = dp(margins[3]);
        }
        PxFitter.fitLinerParams(v, getRealValue(w), getRealValue(h), margins);
    }

    public static void absParams(View v, int w, int h, int x, int y) {
        PxFitter.fitAbsParams(v, dp(w), dp(h), dp(x), dp(y));
    }

    public static void textSize(TextView tv, int textSizeDp) {
        PxFitter.fitTvTextSize(tv, dp(textSizeDp));
    }

    private static int getRealValue(int value) {
        int realValue = 0;
        switch (value) {
            case LayoutParams.WRAP_CONTENT:
            case LayoutParams.MATCH_PARENT: {
                realValue = value;
            }
            break;
            default: {
                realValue = dp(value);
            }
            break;
        }
        return realValue;
    }

    public static float getScale() {
        if (mScaleResolution == ConstantsEx.KInvalidValue) {
            float scaleW = 0;
            float scaleH = 0;

            if (Screen.getWidth() > Screen.getHeight()) {
                // 横屏
                scaleW = Screen.getWidth() / FitConfig.getMaxScreenHeight();
                scaleH = Screen.getHeight() / FitConfig.getMaxScreenWidth();
            } else {
                // 竖屏
                scaleW = Screen.getWidth() / FitConfig.getMaxScreenWidth();
                scaleH = Screen.getHeight() / FitConfig.getMaxScreenHeight();
            }

            mScaleResolution = scaleW < scaleH ? scaleW : scaleH;
        }
        return mScaleResolution;
    }

    /**
     * 把dimen.xml里面的值以DP的形式取出来
     *
     * @param id
     * @return
     */
    public static int getDimensionDp(@DimenRes int id) {
        float px = ResLoader.getDimension(id);
        float dp = px / getDensity();
        return (int) dp;
    }
}
