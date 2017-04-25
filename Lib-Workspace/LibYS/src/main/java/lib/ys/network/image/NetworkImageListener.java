package lib.ys.network.image;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

/**
 * @author yuansui
 */
public class NetworkImageListener {

    /**
     * 图片加载完毕
     */
    public void onImageSet(@Nullable ImageInfo info, @Nullable Bitmap bmp) {
    }

    /**
     * 图片加载失败
     */
    public void onFailure() {
    }
}
