package lib.ys.interfaces;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.view.View;

/**
 * 便捷操作
 *
 * @author yuansui
 */
public interface IOption {
    /**
     * 显示view
     *
     * @param v
     */
    void showView(View v);

    /**
     * 隐藏view(占位)
     *
     * @param v
     */
    void hideView(View v);

    /**
     * 隐藏view(不占位)
     *
     * @param v
     */
    void goneView(View v);

    void startActivity(Class<?> clz);

    void startActivity(Intent intent);

    /**
     * 部分类只有空的impl, 无法实现此功能
     *
     * @param clz
     * @param requestCode
     */
    void startActivityForResult(Class<?> clz, int requestCode);

    void showToast(String content);

    void showToast(@StringRes int... resId);
}
