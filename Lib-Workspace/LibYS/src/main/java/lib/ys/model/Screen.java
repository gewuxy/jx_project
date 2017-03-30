package lib.ys.model;

import android.content.Context;
import android.util.DisplayMetrics;

import lib.ys.AppEx;

public final class Screen {

    private int mWidth = 0; // 屏幕宽度
    private int mHeight = 0; // 屏幕高度
    private float mDensity = 0; // 屏幕密度 0.75 / 1.0 / 1.5 / 2.0
//    private int mDensityDpi = 0; // 像素密度 120 / 160 / 240 / 320

    private static Screen mInst;

    private Screen(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
        mDensity = metrics.density;
//		mDensityDpi = metrics.densityDpi;
    }

    public static int getWidth() {
        checkInst();
        return mInst.mWidth;
    }

    public static int getHeight() {
        checkInst();
        return mInst.mHeight;
    }

    public static float getDensity() {
        checkInst();
        return mInst.mDensity;
    }

    public static void reset() {
        checkInst();
        DisplayMetrics metrics = AppEx.getContext().getResources().getDisplayMetrics();
        mInst.mWidth = metrics.widthPixels;
        mInst.mHeight = metrics.heightPixels;
        mInst.mDensity = metrics.density;
    }

    private static void checkInst() {
        if (mInst == null) {
            mInst = new Screen(AppEx.getContext());
        }
    }
}