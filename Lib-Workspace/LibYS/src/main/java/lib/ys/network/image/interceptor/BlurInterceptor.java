package lib.ys.network.image.interceptor;

import android.content.Context;
import android.graphics.Bitmap;

import lib.ys.util.bmp.BmpUtil;

/**
 * 模糊拦截器
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class BlurInterceptor implements Interceptor {

    private static final int KDefaultRadius = 15;

    private int mRadius;
    private Context mContext;

    public BlurInterceptor(Context context) {
        this(context, KDefaultRadius);
    }

    public BlurInterceptor(Context context, int radius) {
        mContext = context;
        mRadius = radius;
    }

    @Override
    public Bitmap process(Bitmap srcBmp) {
        return BmpUtil.blur(srcBmp, mRadius, mContext);
    }

}
