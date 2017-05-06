package lib.ys.form;

import android.view.View;

/**
 * item里面的单个view点击监听, 供外部activity使用
 *
 * @author yuansui
 */
public interface OnFormViewClickListener {

    /**
     * item里面的单个view点击监听
     *
     * @param v
     * @param position
     * @param related  关联
     */
    void onViewClick(View v, int position, Object related);
}
