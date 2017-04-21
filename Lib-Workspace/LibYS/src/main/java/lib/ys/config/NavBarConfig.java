package lib.ys.config;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import lib.ys.decor.DecorViewEx.TNavBarState;

/**
 * 导航栏配置
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

    private static NavBarConfig mInst = null;

    private NavBarConfig() {
    }

    public static NavBarConfig inst() {
        if (mInst == null) {
            mInst = new NavBarConfig();
        }
        return mInst;
    }

    public void heightDp(int dp) {
        mHeightDp = dp;
    }

    public int getHeightDp() {
        return mHeightDp;
    }

    public int getTextSizeLeftDp() {
        return mTextSizeLeftDp;
    }

    public void textSizeLeftDp(int dp) {
        mTextSizeLeftDp = dp;
    }

    public void textSizeMidDp(int dp) {
        mTextSizeMidDp = dp;
    }

    public int getTextSizeMidDp() {
        return mTextSizeMidDp;
    }

    public void textSizeRightDp(int dp) {
        mTextSizeRightDp = dp;
    }

    public int getTextSizeRightDp() {
        return mTextSizeRightDp;
    }

    public int getIconSizeDp() {
        return mIconSizeDp;
    }

    public void iconSizeDp(int dp) {
        mIconSizeDp = dp;
    }

    public int getIconPaddingHorizontalDp() {
        return mIconPaddingHorizontalDp;
    }

    public void iconPaddingHorizontalDp(int dp) {
        mIconPaddingHorizontalDp = dp;
    }

    public int getTextMarginHorizontalDp() {
        return mTextMarginHorizontalDp;
    }

    public void textMarginHorizontalDp(int dp) {
        mTextMarginHorizontalDp = dp;
    }

    @ColorRes
    public int getTextColor() {
        return mTextColorRes;
    }

    public void textColorRes(@ColorRes int color) {
        mTextColorRes = color;
    }

    public void bgRes(@DrawableRes int res) {
        mBgRes = res;
    }

    @DrawableRes
    public int getBgRes() {
        return mBgRes;
    }

    public void bgColorRes(@ColorRes int res) {
        mBgColorRes = res;
    }

    @ColorRes
    public int getBgColorRes() {
        return mBgColorRes;
    }

    public void dividerHeightPx(int px) {
        mDividerHeightPx = px;
    }

    public int getDividerHeightPx() {
        return mDividerHeightPx;
    }

    public void dividerColor(@ColorRes int res) {
        mDividerColorRes = res;
    }

    public int getDividerColorRes() {
        return mDividerColorRes;
    }

    /**
     * 设置icon点击时的背景色
     *
     * @param res
     */
    public void focusBgColorRes(@ColorRes int res) {
        mFocusBgColorRes = res;
    }

    public int getFocusBgColorRes() {
        return mFocusBgColorRes;
    }

    public TNavBarState getState() {
        return mState;
    }

    public void state(TNavBarState state) {
        mState = state;
    }
}
