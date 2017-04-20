package lib.ys.config;

/**
 * 设置缩放适配的基础属性
 *
 * @author yuansui
 */
public class FitConfig {
    public static final float KDefaultMaxScreenWidth = 1080;
    public static final float KDefaultMaxScreenHeight = 1920;

    public static final int KDefaultDpBaseScale = 3;

    private static int mDpBaseScale = KDefaultDpBaseScale;
    private static float mMaxScreenWidth = KDefaultMaxScreenWidth;
    private static float mMaxScreenHeight = KDefaultMaxScreenHeight;

    public static int getDpBaseScale() {
        return mDpBaseScale;
    }

    public static void setDpBaseScale(int scale) {
        mDpBaseScale = scale;
    }

    public static float getMaxScreenWidth() {
        return mMaxScreenWidth;
    }

    public static void setMaxScreenWidth(float w) {
        mMaxScreenWidth = w;
    }

    public static float getMaxScreenHeight() {
        return mMaxScreenHeight;
    }

    public static void setMaxScreenHeight(float h) {
        mMaxScreenHeight = h;
    }

}
