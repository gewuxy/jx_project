package lib.ys.network.image.interceptor;

import android.graphics.Bitmap;

/**
 * 剪切拦截器
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class CutInterceptor implements Interceptor {

    private int mW;
    private int mH;

    public CutInterceptor(int mW, int mH) {
        this.mW = mW;
        this.mH = mH;
    }

    @Override
    public Bitmap process(Bitmap srcBmp) {
        int srcBmpW = srcBmp.getWidth();
        int srcBmpH = srcBmp.getHeight();

        int cutW = mW / srcBmpW;
        int cutH = mH / srcBmpH;

        int startX = (srcBmpW - cutW) / 2;
        int startY = (srcBmpH - cutH) / 2;

        return Bitmap.createBitmap(srcBmp, startX, startY, cutW, cutH, null, false);
    }

}
