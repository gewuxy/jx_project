package lib.ys.util.bmp;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.IntRange;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lib.ys.AppEx;
import lib.ys.LogMgr;
import lib.ys.util.DeviceUtil;
import lib.ys.util.FileUtil;
import lib.ys.util.UIUtil;

public class BmpUtil {

    private static final String TAG = BmpUtil.class.getSimpleName();

    public static Config getBmpConfig(Bitmap bmp) {
        // return bmp.hasAlpha() ? Config.ARGB_8888 : Config.RGB_565;
        return Config.ARGB_8888;
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap     原始图片
     * @param degrees    要旋转的角度
     * @param isVertical 垂直显示图片
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees, boolean isVertical) {
        boolean setRotate = false;
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        if (!isVertical) {
            if (bitmap.getHeight() > bitmap.getWidth()) {
                setRotate = true;
            }
        } else {
            if (bitmap.getHeight() < bitmap.getWidth()) {
                setRotate = true;
            }
        }
        if (setRotate) {
            return rotateBmpImmutable(bitmap, degrees);
        } else {
            return bitmap;
        }
    }

    /**
     * 根据新的宽高来缩放, 返回的图片是可变的, 可装载入Canvas
     *
     * @param srcBmp
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap resizeBmpMutable(Bitmap srcBmp, float newWidth, float newHeight) {
        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();

        float scaleW = newWidth / (float) width;
        float scaleH = newHeight / (float) height;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleW, scaleH);

        Config config = getBmpConfig(srcBmp);
        Bitmap retBmp = Bitmap.createBitmap((int) newWidth, (int) newHeight, config);
        Canvas c = createCanvas(retBmp);
        c.drawBitmap(srcBmp, matrix, null);
        c = null;

        return retBmp;
    }

    private static final float KMinValue = 1;

    /**
     * 根据比例缩放, 返回的图片是可变的, 可装载入Canvas
     *
     * @param srcBmp
     * @param scale
     * @return
     */
    public static Bitmap resizeBmpMutable(Bitmap srcBmp, float scale) {
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        float newWidth = srcBmp.getWidth() * scale;
        float newHeight = srcBmp.getHeight() * scale;

        if (newWidth < KMinValue) {
            newWidth = KMinValue;
        }

        if (newHeight < KMinValue) {
            newHeight = KMinValue;
        }

        Bitmap retBmp = Bitmap.createBitmap((int) newWidth, (int) newHeight, BmpUtil.getBmpConfig(srcBmp));
        Canvas c = createCanvas(retBmp);
        c.drawBitmap(srcBmp, matrix, null);

        return retBmp;
    }

    /**
     * 同时缩放和旋转
     *
     * @param srcBmp 原图片
     * @param scale  比例
     * @param degree 角度
     * @return 不可变的图片(如果角度为0, scale为1, 那么返回的就是原图)
     */
    public static Bitmap resizeAndRotateBmpImmutable(Bitmap srcBmp, float scale, int degree) {

        Matrix matrix = new Matrix();
        matrix.preScale(scale, scale);
        matrix.postRotate(degree);

        return Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);
    }

    public static Bitmap rotateBmpImmutable(Bitmap srcBmp, int degrees) {

        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);

        /**
         * 不建议使用系统的createScaleBitmap, 不是最清晰的
         */
        return Bitmap.createBitmap(srcBmp, 0, 0, width, height, matrix, true);
    }

    public static Canvas createCanvas(Bitmap bmp) {
        Canvas canvas = new Canvas(bmp);
        UIUtil.setCanvasAntialias(canvas);
        return canvas;
    }

    public static byte[] compressJpeg(Bitmap bmp, int quality) {
        return compress(bmp, quality, CompressFormat.JPEG);
    }

    public static byte[] compressPng(Bitmap bmp, int quality) {
        return compress(bmp, quality, CompressFormat.PNG);
    }

    /**
     * 图片转换成字节数
     *
     * @param bmp
     * @param quality
     * @param format
     * @return
     */
    public static byte[] compress(Bitmap bmp, int quality, CompressFormat format) {
        ByteArrayOutputStream baos = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
            bmp.compress(format, quality, baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            LogMgr.e(TAG, "compress", e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    LogMgr.e(TAG, "compress", e);
                }
            }
        }

        return bytes;
    }

    public static boolean compressPng(Bitmap bmp, File destFile, int quality) {
        return compress(bmp, destFile, quality, CompressFormat.PNG);
    }

    public static boolean compressJpeg(Bitmap bmp, File destFile, int quality) {
        return compress(bmp, destFile, quality, CompressFormat.JPEG);
    }

    public static boolean compress(Bitmap bmp, File destFile, int quality, CompressFormat format) {
        FileOutputStream fos = null;
        boolean result = false;

        try {
            fos = new FileOutputStream(destFile);
            result = bmp.compress(format, quality, fos);
        } catch (OutOfMemoryError error) {
            LogMgr.e(TAG, "compress" + destFile.toString(), error);
            System.gc();
        } catch (Exception e) {
            LogMgr.e(TAG, "compress", e);
        } finally {
            FileUtil.closeOutStream(fos);
        }

        return result;
    }

    /**
     * 根据蒙版叠加图片, 白色保留黑色过滤, 其他颜色自动根据色级调整透明度
     *
     * @param srcBmp  原图
     * @param maskBmp 蒙版图
     * @return 叠加后的图片
     */
    public static Bitmap createFilterBmpByMask(Bitmap srcBmp, Bitmap maskBmp) {

        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();
        int[] bmpPixArray = new int[width * height];
        int[] maskPixArray = new int[width * height];
        srcBmp.getPixels(bmpPixArray, 0, width, 0, 0, width, height);
        maskBmp.getPixels(maskPixArray, 0, width, 0, 0, width, height);

        int color = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        int maskColor = 0;
        int maskAlpha = 0;

        for (int i = 0; i < bmpPixArray.length; i++) {
            color = bmpPixArray[i];
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);

            maskColor = maskPixArray[i];
            maskAlpha = Color.red(maskColor);
            bmpPixArray[i] = Color.argb(maskAlpha, red, green, blue);
        }

        return Bitmap.createBitmap(bmpPixArray, width, height, Config.ARGB_8888);
    }

    /**
     * 对图像进行高斯模糊处理.<strong>该算法耗时</strong>
     *
     * @param source
     * @param radius 模糊半径，数值越大越模糊
     * @return
     */
    private static Bitmap fastBlur(Bitmap source, @IntRange(from = 1, to = 25) int radius) {
        Bitmap bitmap;
        if (source.isMutable()) {
            bitmap = source;
        } else {
            bitmap = source.copy(source.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackPointer;
        int stackStart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackPointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[(stackPointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackPointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[stackPointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static Bitmap rsBlur(Bitmap bitmap, @IntRange(from = 1, to = 25) int radius, Context context) {
        RenderScript rs = null;
        try {
            rs = RenderScript.create(context);
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs,
                    bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            blur.setInput(input);
            blur.setRadius(radius);
            blur.forEach(output);
            output.copyTo(bitmap);
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }

        return bitmap;
    }

    /**
     * 模糊算法
     *
     * @param bitmap
     * @param radius
     * @param context
     * @return 如果是mutable的图片, 返回的是原图
     */
    public static Bitmap blur(Bitmap bitmap, @IntRange(from = 1, to = 25) int radius, Context context) {
        if (DeviceUtil.getSDKVersion() >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return rsBlur(bitmap, radius, context);
        } else {
            return fastBlur(bitmap, radius);
        }
    }

    public static int calcSampleSize(Options opts, int reqWidth, int reqHeight) {
        int sample = computeSampleSize(opts, reqWidth > reqHeight ? reqHeight : reqWidth, reqWidth * reqHeight);
        return sample;
    }

    private static int computeSampleSize(Options opts, int minSideLength, int maxNumOfPixels) {
        int initialSize = getSimpleSize(opts, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static final int KUnConstrained = -1;

    /**
     * 计算初始的sample size
     */
    public static int getSimpleSize(Options opts, int minSideLength, int maxNumOfPixels) {
        double w = opts.outWidth;
        double h = opts.outHeight;

        int lowerBound = (maxNumOfPixels == KUnConstrained) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == KUnConstrained) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        int initSize;
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            initSize = lowerBound;
        } else {
            if ((maxNumOfPixels == KUnConstrained) && (minSideLength == KUnConstrained)) {
                initSize = 1;
            } else if (minSideLength == KUnConstrained) {
                initSize = lowerBound;
            } else {
                initSize = upperBound;
            }
        }

        int roundedSize;
        if (initSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * 保存图片
     *
     * @param bitmap  要保存的bmp
     * @param file    文件
     * @param format  图片格式
     * @param quality 图片质量
     * @return 是否成功保存
     */
    public static boolean save(Bitmap bitmap, File file, CompressFormat format, int quality) {
        boolean ret = true;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(format, quality, fileOutputStream);
        } catch (Exception e) {
            LogMgr.e(TAG, e);
            ret = false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    LogMgr.e(TAG, e);
                }
            }
        }
        return ret;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    public static Bitmap copy(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }
        return bmpSrc.copy(Config.ARGB_8888, true);
    }

    public static Options getOptions() {
        return new Options();
    }

    public static InputStream openInputStream(Uri uri) throws Exception {
        if (uri == null) {
            return null;
        }

        String scheme = uri.getScheme();
        InputStream stream = null;

        if (scheme == null || ContentResolver.SCHEME_FILE.equals(scheme)) {
            // from file
            stream = new FileInputStream(uri.getPath());
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            // from content
            stream = AppEx.ct().getContentResolver().openInputStream(uri);
        }
        return stream;
    }

    public static void recycle(Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
        }
    }

    /**
     * 读取Exif属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return 旋转的角度
     * @author Daisw
     */
    public static int getExifDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Throwable t) {
            LogMgr.e(TAG, "getImageDegree", t);
        }
        return degree;
    }
}
