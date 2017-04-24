package lib.ys.config;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import lib.ys.decor.DecorViewEx.TNavBarState;

/**
 * 导航栏配置
 * 单例模式, 通过内部Builder类进行构造
 *
 * @author yuansui
 */
public class NavBarConfig {

    private int mHeightDp = 0;
    private int mTextSizeLeftDp = 0;
    private int mTextSizeMidDp = 0;
    private int mTextSizeRightDp = 0;
    private int mIconSizeDp = 0;
    private int mIconPaddingHorizontalDp = 0;
    private int mTextMarginHorizontalDp = 0;
    private int mDividerHeightPx = 0;

    @ColorRes
    private int mTextColorRes = 0;

    @DrawableRes
    private int mBgRes = 0;

    @ColorRes
    private int mBgColorRes = 0;

    @ColorRes
    private int mDividerColorRes = 0;

    @ColorRes
    private int mFocusBgColorRes = 0;

    private TNavBarState mState = TNavBarState.linear;

    private NavBarConfig() {
    }

    public int getHeightDp() {
        return mHeightDp;
    }

    public int getTextSizeLeftDp() {
        return mTextSizeLeftDp;
    }

    public int getTextSizeMidDp() {
        return mTextSizeMidDp;
    }

    public int getTextSizeRightDp() {
        return mTextSizeRightDp;
    }

    public int getIconSizeDp() {
        return mIconSizeDp;
    }

    public int getIconPaddingHorizontalDp() {
        return mIconPaddingHorizontalDp;
    }

    public int getTextMarginHorizontalDp() {
        return mTextMarginHorizontalDp;
    }

    @ColorRes
    public int getTextColor() {
        return mTextColorRes;
    }

    @DrawableRes
    public int getBgRes() {
        return mBgRes;
    }

    @ColorRes
    public int getBgColorRes() {
        return mBgColorRes;
    }

    public int getDividerHeightPx() {
        return mDividerHeightPx;
    }

    public int getDividerColorRes() {
        return mDividerColorRes;
    }

    public int getFocusBgColorRes() {
        return mFocusBgColorRes;
    }

    public TNavBarState getState() {
        return mState;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int mHeightDp = 0;
        private int mTextSizeLeftDp = 0;
        private int mTextSizeMidDp = 0;
        private int mTextSizeRightDp = 0;
        private int mIconSizeDp = 0;
        private int mIconPaddingHorizontalDp = 0;
        private int mTextMarginHorizontalDp = 0;
        private int mDividerHeightPx = 0;

        @ColorRes
        private int mTextColorRes = 0;

        @DrawableRes
        private int mBgRes = 0;

        @ColorRes
        private int mBgColorRes = 0;

        @ColorRes
        private int mDividerColorRes = 0;

        @ColorRes
        private int mFocusBgColorRes = 0;

        private TNavBarState mState = TNavBarState.linear;

        private Builder() {
        }

        public Builder heightDp(int dp) {
            mHeightDp = dp;
            return this;
        }

        public Builder textSizeLeftDp(int dp) {
            mTextSizeLeftDp = dp;
            return this;
        }

        public Builder textSizeMidDp(int dp) {
            mTextSizeMidDp = dp;
            return this;
        }

        public Builder textSizeRightDp(int dp) {
            mTextSizeRightDp = dp;
            return this;
        }

        public Builder iconSizeDp(int dp) {
            mIconSizeDp = dp;
            return this;
        }

        public Builder iconPaddingHorizontalDp(int dp) {
            mIconPaddingHorizontalDp = dp;
            return this;
        }

        public Builder textMarginHorizontalDp(int dp) {
            mTextMarginHorizontalDp = dp;
            return this;
        }

        public Builder textColorRes(@ColorRes int color) {
            mTextColorRes = color;
            return this;
        }

        public Builder bgRes(@DrawableRes int res) {
            mBgRes = res;
            return this;
        }

        public Builder bgColorRes(@ColorRes int res) {
            mBgColorRes = res;
            return this;
        }

        public Builder dividerHeightPx(int px) {
            mDividerHeightPx = px;
            return this;
        }

        public Builder dividerColor(@ColorRes int res) {
            mDividerColorRes = res;
            return this;
        }

        /**
         * 设置icon点击时的背景色
         *
         * @param res
         */
        public Builder focusBgColorRes(@ColorRes int res) {
            mFocusBgColorRes = res;
            return this;
        }

        public Builder state(TNavBarState state) {
            mState = state;
            return this;
        }

        /**
         * 直接给单例赋值, 不进行返回
         */
        public NavBarConfig build() {
            NavBarConfig c = new NavBarConfig();

            c.mHeightDp = mHeightDp;
            c.mTextSizeLeftDp = mTextSizeLeftDp;
            c.mTextSizeMidDp = mTextSizeMidDp;
            c.mTextSizeRightDp = mTextSizeRightDp;
            c.mIconSizeDp = mIconSizeDp;
            c.mIconPaddingHorizontalDp = mIconPaddingHorizontalDp;
            c.mTextMarginHorizontalDp = mTextMarginHorizontalDp;
            c.mDividerHeightPx = mDividerHeightPx;
            c.mTextColorRes = mTextColorRes;
            c.mBgRes = mBgRes;
            c.mDividerColorRes = mDividerColorRes;
            c.mFocusBgColorRes = mFocusBgColorRes;
            c.mState = mState;
            c.mBgColorRes = mBgColorRes;

            return c;
        }
    }
}
