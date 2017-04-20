package lib.ys.config;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.decor.ErrorDecorEx;
import lib.ys.util.res.ResLoader;

/**
 * @author yuansui
 */
public class AppConfig {

    /**
     * 页面刷新的方式
     */
    @IntDef({
            RefreshWay.un_know,
            RefreshWay.dialog,
            RefreshWay.embed,
            RefreshWay.swipe,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RefreshWay {
        int un_know = -1;
        int dialog = 0;
        int embed = 1;
        int swipe = 2;
    }

    @ColorInt
    private static int mAppBgColor = 0;

    @DrawableRes
    private static int mAppBgDrawableId = 0;

    private static Class<? extends ErrorDecorEx> mErrorDecorClz = null;

    private static boolean mEnableSwipeFinish = false;

    /**
     * 初始化的加载样式
     */
    @RefreshWay
    private static int mInitRefreshWay = RefreshWay.dialog;

    // 是否使用沉浸式状态栏
    private static boolean mEnableFlatBar = false;

    public static void appBgColor(@ColorInt int color) {
        mAppBgColor = color;
    }

    public static void appBgColorId(@ColorRes int id) {
        mAppBgColor = ResLoader.getColor(id);
    }

    public static void appBgDrawable(@DrawableRes int id) {
        mAppBgDrawableId = id;
    }

    public static Class<? extends ErrorDecorEx> getErrorDecorClz() {
        return mErrorDecorClz;
    }

    public static void errorDecorClz(Class<? extends ErrorDecorEx> clz) {
        mErrorDecorClz = clz;
    }

    public static int getAppBgColor() {
        return mAppBgColor;
    }

    public static int getAppBgDrawableId() {
        return mAppBgDrawableId;
    }

    @TargetApi(VERSION_CODES.KITKAT)
    /**
     * 设置沉浸式状态栏是否可用
     * @param enable
     */
    public static void enableFlatBar(boolean enable) {
        mEnableFlatBar = enable;
    }

    @TargetApi(VERSION_CODES.KITKAT)
    public static boolean isFlatBarEnabled() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            return mEnableFlatBar;
        } else {
            return false;
        }
    }

    /**
     * 设置初始化数据的时候使用的刷新模式
     *
     * @param way
     */
    public static void initRefreshWay(@RefreshWay int way) {
        mInitRefreshWay = way;
    }

    @RefreshWay
    public static int getInitRefreshWay() {
        return mInitRefreshWay;
    }

    public static boolean isSwipeFinishEnabled() {
        return mEnableSwipeFinish;
    }

    /**
     * 是否使用滑动退出activity, 使用的时候需要注意事项:
     * 1. 底层的main activity一定要使用传统不透明的theme
     * 2. application theme使用{@link lib.ys.R.style#AppTheme_SwipeBack}
     *
     * @param enable
     */
    public static void enableSwipeFinish(boolean enable) {
        mEnableSwipeFinish = enable;
    }

}
