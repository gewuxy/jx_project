package lib.ys.adapter.interfaces;

import android.view.View;

/**
 * @author yuansui
 */
public interface IViewHolder {

    View getConvertView();

    /**
     * 在BaseView里find
     *
     * @param id
     * @return
     */
    <T extends View> T findView(int id);

    /**
     * 获取对应id的View, 并放入map里持有
     *
     * @param id
     * @return
     */
    <T extends View> T getView(int id);
}
