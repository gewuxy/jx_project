package lib.ys.util.bmp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.annotation.DrawableRes;

import lib.ys.model.FileSuffix;
import lib.ys.util.res.ResLoader;
import lib.ys.util.res.ResUtil.ResDefType;


/**
 * 一定要继承自AppEx的Application才能使用这个工具类
 */
public class BmpLoader {

    /***************************
     * 关于res id的加载方式
     * *************************
     */

    /**
     * 加载R图片
     *
     * @param id
     * @return
     */
    public static Bitmap load(@DrawableRes int id) {
        return ResLoader.getBitmap(id);
    }

    public static Bitmap load(@DrawableRes int id, int w, int h) {
        Options opts = ResLoader.getBmpInfo(id);
        opts.inSampleSize = BmpUtil.getSampleSizeByWH(opts, w, h);
        opts.inJustDecodeBounds = false;
        return ResLoader.getBitmap(id, opts);
    }

    public static Bitmap load(@DrawableRes int id, float scale) {
        Bitmap tmpBmp = load(id);
        Bitmap retBmp = BmpUtil.resizeBmpMutable(tmpBmp, scale);
        BmpUtil.recycle(tmpBmp);
        return retBmp;
    }

    public static Bitmap load(@DrawableRes int id, int sampleSize) {
        Options opt = ResLoader.getBmpInfo(id);
        opt.inSampleSize = sampleSize;
        opt.inJustDecodeBounds = false;
        return ResLoader.getBitmap(id, opt);
    }

    public static Bitmap load(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static Bitmap load(byte[] data, int maxNumOfPixels) {
        Options opts = new Options();

        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        opts.inSampleSize = BmpUtil.getSimpleSizeByRule(opts, maxNumOfPixels);
        opts.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
    }

    /***************************
     * 关于mipmap名称的加载方式
     * *************************
     */

    /**
     * 加载mipmap图片
     *
     * @param name
     * @return
     */
    public static Bitmap loadMipmap(String name) {
        return load(getMipmapId(name));
    }

    public static int getMipmapId(String name) {
        return ResLoader.getIdentifier(name, ResDefType.mipmap);
    }

    /***************************
     * 关于drawable名称的加载方式
     * *************************
     */

    /**
     * 加载drawable图片
     *
     * @param name
     * @return
     */
    public static Bitmap loadDrawable(String name) {
        return load(getDrawableId(name));
    }

    public static int getDrawableId(String name) {
        return ResLoader.getIdentifier(name, ResDefType.drawable);
    }

    public static Bitmap loadDrawable(String name, int w, int h) {
        return load(getDrawableId(name), w, h);
    }

    public static Bitmap loadDrawable(String name, int sampleSize) {
        return load(getDrawableId(name), sampleSize);
    }

    /***************************
     * 关于路径的加载方式
     * *************************
     */

    /**
     * 获取SD卡图片
     *
     * @param path 图片完整路径, 带后缀
     * @return
     */
    public static Bitmap loadPath(String path) {
        return ResLoader.getBitmap(path);
    }

    public static Bitmap loadPath(String path, int w, int h) {
        Options opt = ResLoader.getBmpInfo(path);
        opt.inSampleSize = BmpUtil.getSampleSizeByWH(opt, w, h);
        opt.inJustDecodeBounds = false;
        return ResLoader.getBitmap(path, opt);
    }

    public static Bitmap loadPath(String path, int sampleSize) {
        Options opt = ResLoader.getBmpInfo(path);
        opt.inSampleSize = sampleSize;
        opt.inJustDecodeBounds = false;
        return ResLoader.getBitmap(path, opt);
    }

    /**
     * @param path
     * @param suffix {@link FileSuffix}
     * @return
     */
    public static Bitmap loadPath(String path, @FileSuffix String suffix) {
        return ResLoader.getBitmap(path + suffix);
    }

    /**
     * 根据最大的像素来获取图片
     *
     * @param path
     * @param maxNumOfPixels
     * @return
     */
    public static Bitmap loadPathByPixels(String path, int maxNumOfPixels) {
        return loadPath(path, BmpUtil.getSimpleSizeByRule(ResLoader.getBmpInfo(path), maxNumOfPixels));
    }

}
