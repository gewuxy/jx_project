package lib.ys.ui.interfaces.listener.scrollable;

import android.support.annotation.NonNull;

import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.other.NavBar;

/**
 * children view的所有方法
 *
 * @author yuansui
 */
public interface OnListScrollableListener<T, A extends IAdapter<T>>
        extends BaseOnScrollableListener<T> {

    @NonNull
    A getAdapter();

    void setOnAdapterClickListener(OnAdapterClickListener listener);

    void setOnScrollListener(OnScrollMixListener listener);

    int getItemRealPosition(int position);

    int getFirstVisiblePosition();

    int getHeaderViewPosition();

    /**
     * 设置titleBar根据list的滑动来改变alpha
     * 此方法和{@link #setOnScrollListener(OnScrollMixListener)}冲突, 只能使用其中一个
     *
     * @param height 最大高度时alpha为255
     * @param navBar 需要改变的titleBar
     */
    void changeAlphaByScroll(int height, NavBar navBar);

    void setDividerHeight(int height);
}
