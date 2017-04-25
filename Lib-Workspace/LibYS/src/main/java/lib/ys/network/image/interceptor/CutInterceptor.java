package lib.ys.network.image.interceptor;

import android.graphics.Bitmap;

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

        Bitmap bmp = Bitmap.createBitmap(srcBmp, mX, mY, mW, mH, null, false);

        /*if (srcBmp != null && !srcBmp.equals(bmp) && !srcBmp.isRecycled()) {
            srcBmp.recycle();
            srcBmp = null;
        }*/

        return bmp;
    }

}
