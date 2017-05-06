package lib.ys.util.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import lib.ys.AppEx;
import lib.ys.util.res.ResUtil.ResDefType;

/**
 * 加载资源管理器
 * 一定要继承自ApplicationEx的Application才能使用这个工具类
 */
public class ResLoader {

    private static Context mCt = null;

    static {
        mCt = AppEx.ct();
    }

    public static int getIdentifier(String name, @ResDefType String type) {
        return ResUtil.getIdentifier(mCt, name, type);
    }

    public static String getString(@StringRes int resId) {
        return ResUtil.getString(mCt, resId);
    }

    public static String[] getStringArray(int resId) {
        return ResUtil.getStringArray(mCt, resId);
    }

    public static Bitmap getBitmap(@DrawableRes int resId) {
        return ResUtil.getBitmap(mCt, resId);
    }

    public static Bitmap getBitmap(@DrawableRes int resId, Options opt) {
        return ResUtil.getBitmap(mCt, resId, opt);
    }

    public static Bitmap getBitmap(String filePath) {
        return ResUtil.getBitmap(filePath);
    }

    public static Bitmap getBitmap(String filePath, Options opt) {
        return ResUtil.getBitmap(filePath, opt);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return ResUtil.getDrawable(mCt, resId);
    }

    /**
     * 不载入内存, 只读取图片的信息
     *
     * @param resId
     * @return Options对象opt 使用方法: 如获取高度, opt.outHeight
     */
    public static Options getBmpInfo(@DrawableRes int resId) {
        return ResUtil.getBmpInfo(mCt, resId);
    }

    /**
     * 不载入内存, 只读取图片的信息
     *
     * @return Options对象opt 使用方法: 如获取高度, opt.outHeight
     */
    public static Options getBmpInfo(String filePath) {
        return ResUtil.getBmpInfo(filePath);
    }

    public static int getColor(@ColorRes int resId) {
        return ResUtil.getColor(mCt, resId);
    }

    public static ColorStateList getColorStateList(@ColorRes int resId) {
        return ResUtil.getColorStateList(mCt, resId);
    }

    public static float getDimension(@DimenRes int resId) {
        return ResUtil.getDimension(mCt, resId);
    }

    public static String getRawContent(@RawRes int resId) {
        return ResUtil.getRawContent(mCt, resId);
    }

    public static Integer getInteger(@IntegerRes int resId) {
        return ResUtil.getInteger(mCt, resId);
    }
}
