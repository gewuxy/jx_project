package lib.ys.network.image.interceptor;

import android.graphics.Bitmap;

import lib.ys.util.bmp.BmpUtil;

/**
 * 获取图片拦截器
 * 创建对象的时候处理获取到的图片
 *
 * @auther yuansui
 * @since 2017/4/25
 */
abstract public class FetchInterceptor implements Interceptor {

    @Override
    public final Bitmap process(Bitmap srcBmp) {
        // 为了防止外部的处理影响到内部的处理, copy一份
        onResult(BmpUtil.copy(srcBmp));
        return srcBmp;
    }

    /**
     * @param bmp 经过copy的图片
     */
    abstract protected void onResult(Bitmap bmp);
}
