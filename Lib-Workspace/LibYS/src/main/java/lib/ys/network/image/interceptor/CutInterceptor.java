package lib.ys.network.image.interceptor;

import android.graphics.Bitmap;

import lib.ys.util.bmp.BmpUtil;

/**
 * 剪切拦截器
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class CutInterceptor implements Interceptor {

    private int mX;
    private int mY;
    private int mW;
    private int mH;

    /**
     * @param x      The x coordinate of the first pixel in source
     * @param y      The y coordinate of the first pixel in source
     * @param width  The number of pixels in each row
     * @param height The number of rows
     */
    public CutInterceptor(int x, int y, int width, int height) {
        mX = x;
        mY = y;
        mW = width;
        mH = height;
    }

    @Override
    public Bitmap process(Bitmap srcBmp) {
        return BmpUtil.resizeBmpMutable(srcBmp, mW, mH);
    }
}
