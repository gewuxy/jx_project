package lib.ys.config;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

import lib.ys.decor.DecorViewEx.TTitleBarState;
import lib.ys.util.res.ResLoader;

/**
 * @author yuansui
 */
public class TitleBarConfig {

    private static int mHeightDp = 0;
    private static int mTextSizeLeftDp = 0;
    private static int mTextSizeMidDp = 0;
    private static int mTextSizeRightDp = 0;
    private static int mIconSizeDp = 0;
    private static int mIconPaddingHorizontalDp = 0;
    private static int mTextMarginHorizontalDp = 0;
    private static int mDividerHeightPx = 0;

    @ColorInt
    private static int mTextColor = 0;

    @ColorInt
    private static int mBgColor = 0;

    @DrawableRes
    private static int mBgDrawableId = 0;

    @ColorInt
    private static int mDividerColor = 0;

    @ColorInt
    private static int mViewClickBgColor = 0;

    private static TTitleBarState mState = TTitleBarState.linear;


    public static void heightDp(int dp) {
        mHeightDp = dp;
    }

    public static int getHeightDp() {
        return mHeightDp;
    }

    public static int getTextSizeLeftDp() {
        return mTextSizeLeftDp;
    }

    public static void textSizeLeftDp(int dp) {
        mTextSizeLeftDp = dp;
    }

    public static void textSizeMidDp(int dp) {
        mTextSizeMidDp = dp;
    }

    public static int getTextSizeMidDp() {
        return mTextSizeMidDp;
    }

    public static void textSizeRightDp(int dp) {
        mTextSizeRightDp = dp;
    }

    public static int getTextSizeRightDp() {
        return mTextSizeRightDp;
    }

    public static int getIconSizeDp() {
        return mIconSizeDp;
    }

    public static void iconSizeDp(int dp) {
        mIconSizeDp = dp;
    }

    public static int getIconPaddingHorizontalDp() {
        return mIconPaddingHorizontalDp;
    }

    public static void iconPaddingHorizontalDp(int dp) {
        mIconPaddingHorizontalDp = dp;
    }

    public static int getTextMarginHorizontalDp() {
        return mTextMarginHorizontalDp;
    }

    public static void textMarginHorizontalDp(int dp) {
        mTextMarginHorizontalDp = dp;
    }

    public static int getTextColor() {
        return mTextColor;
    }

    public static void textColor(@ColorInt int color) {
        mTextColor = color;
    }

    public static void bgColor(@ColorInt int color) {
        mBgColor = color;
    }

    public static int getBgColor() {
        return mBgColor;
    }

    /**
     * 获取背景资源id
     *
     * @return 默认无资源时为0
     */
    public static int getBgDrawableId() {
        return mBgDrawableId;
    }

    /**
     * 设置背景资源id
     *
     * @param id
     */
    public static void bgDrawableId(@DrawableRes int id) {
        mBgDrawableId = id;
    }

    public static void dividerHeightPx(int px) {
        mDividerHeightPx = px;
    }

    public static int getDividerHeightPx() {
        return mDividerHeightPx;
    }

    /**
     * @param color {@link ResLoader#getColor(int)} / {@link Color}
     */
    public static void dividerColor(@ColorInt int color) {
        mDividerColor = color;
    }

    public static int getDividerColor() {
        return mDividerColor;
    }

    /**
     * 设置icon点击时的背景色
     *
     * @param color {@link ResLoader#getColor(int)} / {@link Color}
     */
    public static void viewClickBgColor(@ColorInt int color) {
        mViewClickBgColor = color;
    }

    public static int getViewClickBgColor() {
        return mViewClickBgColor;
    }

    public static TTitleBarState getState() {
        return mState;
    }

    public static void state(TTitleBarState state) {
        mState = state;
    }
}
