package lib.ys.config;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.util.UIUtil;

/**
 * 导航栏配置
 * 通过内部Builder类进行构造
 *
 * @author yuansui
 */
public class NavBarConfig {

    private int mHeight;
    private int mTextSizeLeft;
    private int mTextSizeMid;
    private int mTextSizeRight;
    private int mIconSize;
    private int mIconPaddingHorizontal;
    private int mTextMarginHorizontal;
    private int mDividerHeight;

    @ColorRes
    private int mTextColorRes;

    @DrawableRes
    private int mBgRes;

    @ColorRes
    private int mBgColorRes;

    @ColorRes
    private int mDividerColorRes;

    @ColorRes
    private int mFocusBgColorRes;

    @DrawableRes
    private int mFocusBgDrawableRes;

    private TNavBarState mState = TNavBarState.linear;

    private NavBarConfig() {
    }

    public int getHeight() {
        return mHeight;
    }

    public int getTextSizeLeft() {
        return mTextSizeLeft;
    }

    public int getTextSizeMid() {
        return mTextSizeMid;
    }

    public int getTextSizeRight() {
        return mTextSizeRight;
    }

    public int getIconSize() {
        return mIconSize;
    }

    public int getIconPaddingHorizontal() {
        return mIconPaddingHorizontal;
    }

    public int getTextMarginHorizontal() {
        return mTextMarginHorizontal;
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

    public int getDividerHeight() {
        return mDividerHeight;
    }

    public int getDividerColorRes() {
        return mDividerColorRes;
    }

    public int getFocusBgColorRes() {
        return mFocusBgColorRes;
    }

    public int getFocusBgDrawableRes() {
        return mFocusBgDrawableRes;
    }

    public TNavBarState getState() {
        return mState;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int mHeightDp;
        private int mTextSizeLeftDp;
        private int mTextSizeMidDp;
        private int mTextSizeRightDp;
        private int mIconSizeDp;
        private int mIconPaddingHorizontalDp;
        private int mTextMarginHorizontalDp;
        private int mDividerHeightPx;

        @ColorRes
        private int mTextColorRes;

        @DrawableRes
        private int mBgRes;

        @ColorRes
        private int mBgColorRes;

        @ColorRes
        private int mDividerColorRes;

        @ColorRes
        private int mFocusBgColorRes;

        @DrawableRes
        private int mFocusBgDrawableRes;

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

        public Builder focusBgDrawableRes(@DrawableRes int res) {
            mFocusBgDrawableRes = res;
            return this;
        }

        public Builder state(TNavBarState state) {
            mState = state;
            return this;
        }

        public NavBarConfig build() {
            NavBarConfig c = new NavBarConfig();

            c.mHeight = convertDp(mHeightDp);

            c.mTextSizeLeft = convertDp(mTextSizeLeftDp);
            c.mTextSizeMid = convertDp(mTextSizeMidDp);
            c.mTextSizeRight = convertDp(mTextSizeRightDp);
            c.mTextMarginHorizontal = convertDp(mTextMarginHorizontalDp);
            c.mTextColorRes = mTextColorRes;

            c.mIconSize = convertDp(mIconSizeDp);
            c.mIconPaddingHorizontal = convertDp(mIconPaddingHorizontalDp);

            c.mDividerHeight = mDividerHeightPx;
            c.mDividerColorRes = mDividerColorRes;

            c.mBgRes = mBgRes;
            c.mBgColorRes = mBgColorRes;

            c.mFocusBgColorRes = mFocusBgColorRes;
            c.mFocusBgDrawableRes = mFocusBgDrawableRes;

            c.mState = mState;

            return c;
        }

        private int convertDp(int val) {
            if (val == 0) {
                return val;
            }
            return UIUtil.dpToPx(val);
        }
    }
}
