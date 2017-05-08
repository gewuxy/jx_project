package lib.ys.config;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.ui.decor.ErrorDecorEx;

/**
 * App整体配置
 *
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

    @DrawableRes
    private int mBgRes = 0;

    @ColorRes
    private int mBgColorRes = 0;

    private Class<? extends ErrorDecorEx> mErrorDecorClz = null;

    private boolean mEnableSwipeFinish = false;

    /**
     * 初始化的加载样式
     */
    @RefreshWay
    private int mInitRefreshWay = RefreshWay.dialog;

    // 是否使用沉浸式状态栏
    private boolean mEnableFlatBar = false;

    private AppConfig() {
    }

    public Class<? extends ErrorDecorEx> getErrorDecorClz() {
        return mErrorDecorClz;
    }

    @DrawableRes
    public int getBgRes() {
        return mBgRes;
    }

    @ColorRes
    public int getBgColorRes() {
        return mBgColorRes;
    }

    @TargetApi(VERSION_CODES.KITKAT)
    public boolean isFlatBarEnabled() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            return mEnableFlatBar;
        } else {
            return false;
        }
    }

    @RefreshWay
    public int getInitRefreshWay() {
        return mInitRefreshWay;
    }

    public boolean isSwipeFinishEnabled() {
        return mEnableSwipeFinish;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        @DrawableRes
        private int mBgRes = 0;

        @ColorRes
        private int mBgColorRes = 0;

        private Class<? extends ErrorDecorEx> mErrorDecorClz = null;

        private boolean mEnableSwipeFinish = false;

        /**
         * 初始化的加载样式
         */
        @RefreshWay
        private int mInitRefreshWay = RefreshWay.dialog;

        // 是否使用沉浸式状态栏
        private boolean mEnableFlatBar = false;

        private Builder() {
        }

        public Builder bgRes(@DrawableRes int res) {
            mBgRes = res;
            return this;
        }

        public Builder bgColorRes(@ColorRes int res) {
            mBgColorRes = res;
            return this;
        }

        public Builder errorDecorClz(Class<? extends ErrorDecorEx> clz) {
            mErrorDecorClz = clz;
            return this;
        }

        @TargetApi(VERSION_CODES.KITKAT)
        /**
         * 设置沉浸式状态栏是否可用
         * @param enable
         */
        public Builder enableFlatBar(boolean enable) {
            mEnableFlatBar = enable;
            return this;
        }

        /**
         * 设置初始化数据的时候使用的刷新模式
         *
         * @param way
         */
        public Builder initRefreshWay(@RefreshWay int way) {
            mInitRefreshWay = way;
            return this;
        }

        /**
         * 是否使用滑动退出activity, 使用的时候需要注意事项:
         * 1. 底层的main activity一定要使用传统不透明的theme
         * 2. application theme使用{@link lib.ys.R.style#AppTheme_SwipeBack}
         *
         * @param enable
         */
        public Builder enableSwipeFinish(boolean enable) {
            mEnableSwipeFinish = enable;
            return this;
        }

        public AppConfig build() {
            AppConfig c = new AppConfig();

            c.mBgRes = mBgRes;
            c.mBgColorRes = mBgColorRes;
            c.mErrorDecorClz = mErrorDecorClz;
            c.mEnableSwipeFinish = mEnableSwipeFinish;
            c.mInitRefreshWay = mInitRefreshWay;
            c.mEnableFlatBar = mEnableFlatBar;

            return c;
        }
    }
}
