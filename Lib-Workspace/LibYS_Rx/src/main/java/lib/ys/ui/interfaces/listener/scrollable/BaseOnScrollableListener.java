package lib.ys.ui.interfaces.listener.scrollable;

import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import lib.ys.ui.interfaces.IScrollable;

/**
 * @auther yuansui
 * @since 2017/7/20
 */
public interface BaseOnScrollableListener<T> {

    int getScrollableViewId();

    View getScrollableView();

    IScrollable<T> getScrollable();

    @Nullable
    View createHeaderView();

    @Nullable
    View createFooterView();

    @Nullable
    View createEmptyView();

    void hideFooterView();

    void showFooterView();

    void showHeaderView();

    void hideHeaderView();

    void addEmptyViewIfNonNull();

    void setData(List<T> ts);

    void addItem(T item);

    void addItem(int position, T item);

    void addAll(List<T> data);

    void addAll(int position, List<T> item);

    void invalidate();

    void remove(int position);

    void remove(T item);

    void removeAll();

    List<T> getData();

    int getCount();

    int getLastItemPosition();

    T getItem(int position);

    boolean isEmpty();

    /**
     * PS: ListP里不实现
     * 是否延迟添加emptyView
     * 有时候会因为加载网络的原因, 先显示了emptyView然后再显示loading, 结束后再切换回来, 影响体验
     *
     * @return
     */
    boolean needDelayAddEmptyView();

    boolean enableLongClick();

    /**
     * 排除header和footer的点击区域, 只返回有效的position
     *
     * @param v
     * @param position
     */
    void onItemClick(View v, int position);

    /**
     * 排除header和footer的点击区域, 只返回有效的position
     *
     * @param v
     * @param position
     */
    void onItemLongClick(View v, int position);

    /**
     * 点击整个header区域
     *
     * @param v
     */
    void onHeaderClick(View v);

    /**
     * 点击整个footer区域, 如果footer里的布局没有设置onClick监听的话可以接受到
     *
     * @param v
     */
    void onFooterClick(View v);

    /**
     * 设置当前显示的item(直接跳到)
     *
     * @param position
     */
    void setSelection(int position);

    /**
     * 设置当前显示的item(平滑滚动)
     *
     * @param position
     */
    void smoothScrollToPosition(int position);

    /**
     * 监听adapter数据变化
     */
    void onDataSetChanged();

    View getChildAt(int index);
}
