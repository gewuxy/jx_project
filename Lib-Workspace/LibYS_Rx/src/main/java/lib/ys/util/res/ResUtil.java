package lib.ys.util.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.NinePatch;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.YSLog;

public final class ResUtil {

    private static final String TAG = ResUtil.class.getSimpleName();

    @StringDef({
            ResDefType.id,
            ResDefType.string,
            ResDefType.drawable,
            ResDefType.mipmap,
            ResDefType.layout,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResDefType {
        String id = "id";
        String string = "string";
        String drawable = "drawable";
        String mipmap = "mipmap";
        String layout = "layout";
    }

    private static Options mOpt = new Options();

    static {
        mOpt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mOpt.inDither = false; // 不自动转成16位色
        mOpt.inPurgeable = true;
        mOpt.inInputShareable = true;
    }

    protected static int getIdentifier(Context context, String name, @ResDefType String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    protected static String getString(Context context, int resId) {
        return context.getString(resId);
    }

    protected static String[] getStringArray(Context context, @ArrayRes int resId) {
        return context.getResources().getStringArray(resId);
    }

    protected static Bitmap getBitmap(Context context, @DrawableRes int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId, mOpt);
    }

    protected static Bitmap getBitmap(Context context, @DrawableRes int resId, Options opt) {
        return BitmapFactory.decodeResource(context.getResources(), resId, opt);
    }

    protected static Bitmap getBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath, mOpt);
    }

    protected static Bitmap getBitmap(String filePath, Options opt) {
        return BitmapFactory.decodeFile(filePath, opt);
    }

    protected static Drawable getDrawable(Context context, @DrawableRes int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    /**
     * 不载入内存, 只读取图片的信息
     *
     * @param resId
     * @return Options对象opt 使用方法: 如获取高度, opt.outHeight
     */
    protected static Options getBmpInfo(Context context, @DrawableRes int resId) {
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, opt);
        return opt;
    }

    /**
     * 不载入内存, 只读取图片的信息
     *
     * @return Options对象opt 使用方法: 如获取高度, opt.outHeight
     */
    protected static Options getBmpInfo(String filePath) {
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);
        return opt;
    }

    protected static int getColor(Context context, @ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    protected static ColorStateList getColorStateList(Context context, @ColorRes int resId) {
        return ContextCompat.getColorStateList(context, resId);
//        ColorStateList list = null;
//        try {
//            Resources res = context.getResources();
//            XmlResourceParser xmlParser = res.getXml(resId);
//            list = ColorStateList.createFromXml(res, xmlParser);
//        } catch (Exception e) {
//            LogMgr.e(TAG, "getColorStateList", e);
//        }
//        return list;
    }

    protected static float getDimension(Context context, @DimenRes int resId) {
        return context.getResources().getDimension(resId);
    }

    protected static NinePatch getNinePatchBmp(Context context, @DrawableRes int resId) {
        Bitmap tmpBmp = getBitmap(context, resId);
        return new NinePatch(tmpBmp, tmpBmp.getNinePatchChunk(), null);
    }

    @NonNull
    protected static String getRawContent(Context context, @RawRes int resId) {
        String content = "";
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resId);
            byte buffer[] = new byte[is.available()];
            is.read(buffer);
            content = new String(buffer);
        } catch (IOException e) {
            YSLog.e(TAG, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    YSLog.e(TAG, e);
                }
            }
        }
        return content;
    }

    protected static Integer getInteger(Context context, @IntegerRes int resId) {
        return context.getResources().getInteger(resId);
    }
}
