package lib.ys.network.image.interceptor;

import android.graphics.Bitmap;

import java8.lang.FunctionalInterface;

/**
 * 拦截器
 *
 * @auther yuansui
 * @since 2017/4/25
 */
@FunctionalInterface
public interface Interceptor {
    /**
     * 处理图片
     *
     * @param srcBmp
     * @return
     */
    Bitmap process(Bitmap srcBmp);
}
