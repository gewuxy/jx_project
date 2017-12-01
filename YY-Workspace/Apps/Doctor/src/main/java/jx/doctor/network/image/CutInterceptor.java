package jx.doctor.network.image;

import android.graphics.Bitmap;

import lib.ys.network.image.interceptor.Interceptor;

/**
 * 剪切拦截器
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class CutInterceptor implements Interceptor {

    private static final float KCutScale = 5f;

    private float mW;
    private float mH;

    public CutInterceptor(float w, float h) {
        mW = w;
        mH = h;
    }

    @Override
    public Bitmap process(Bitmap srcBmp) {
        int bmpW = srcBmp.getWidth();
        int bmpH = srcBmp.getHeight();

        float scale = mW / mH;
        float w = bmpW / KCutScale * scale;
        float h = w / scale;

        int startX = (int) ((bmpW - w) / 2);
        int startY = (int) ((bmpH - h) / 2);

        return Bitmap.createBitmap(srcBmp, startX, startY, (int) w, (int) h, null, false);
    }

}
