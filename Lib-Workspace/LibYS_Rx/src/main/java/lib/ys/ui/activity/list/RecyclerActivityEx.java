package lib.ys.ui.activity.list;

import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.interfaces.IScrollable;
import lib.ys.ui.interfaces.impl.scrollable.RecyclerScrollable;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.scrollable.OnRecyclerScrollableListener;
import lib.ys.view.recycler.WrapRecyclerView;

/**
 * 下拉刷新 Recycler view
 *
 * @param <T>
 */
abstract public class RecyclerActivityEx<T, A extends IAdapter<T>>
        extends ActivityEx
        implements OnRecyclerScrollableListener<T, A> {

    private RecyclerScrollable<T, A> mScrollable = new RecyclerScrollable<>(this);

    @Override
    public int getContentViewId() {
        return R.layout.recycler_layout;
    }

    @Override
    public void findViews() {
        mScrollable.findViews(getDecorView(), getScrollableViewId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @Override
    public void setViews() {
        mScrollable.setViews(initLayoutManager(), initItemDecoration(), initItemAnimator());
    }

    /**
     * 设置divider样式
     *
     * @return
     */
    protected ItemDecoration initItemDecoration() {
        return null;
    }

    /**
     * 设置增加删除动画
     *
     * @return
     */
    protected ItemAnimator initItemAnimator() {
        return null;
    }

    /**
     * 设置默认的布局
     *
     * @return
     */
    protected LayoutManager initLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public int getScrollableViewId() {
        return R.id.recycler_view;
    }

    @Override
    public View createHeaderView() {
        return null;
    }

    @Override
    public View createFooterView() {
        return null;
    }

    @Override
    public View createEmptyView() {
        return null;
    }

    @Override
    public A getAdapter() {
        return mScrollable.getAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }

    @Override
    public void hideFooterView() {
        mScrollable.hideFooterView();
    }

    @Override
    public void showFooterView() {
        mScrollable.showFooterView();
    }

    @Override
    public void showHeaderView() {
        mScrollable.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mScrollable.hideHeaderView();
    }

    public void getDataFromNet() {
    }

    @Override
    public boolean enableLongClick() {
        return false;
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return false;
    }

    @Override
    public void setData(List list) {
        mScrollable.setData(list);
    }

    @Override
    public void addItem(T item) {
        mScrollable.addItem(item);
    }

    @Override
    public void addItem(int position, T item) {
        mScrollable.addItem(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mScrollable.addAll(data);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mScrollable.addAll(position, item);
    }

    @Override
    public void invalidate() {
        mScrollable.invalidate();
    }

    @Override
    public void remove(int position) {
        mScrollable.remove(position);
    }

    @Override
    public void remove(T item) {
        mScrollable.remove(item);
    }

    @Override
    public void removeAll() {
        mScrollable.removeAll();
    }

    @Override
    public List<T> getData() {
        return mScrollable.getData();
    }

    @Override
    public int getCount() {
        return mScrollable.getCount();
    }

    @Override
    public int getLastItemPosition() {
        return mScrollable.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mScrollable.getItem(position);
    }

    @Override
    public boolean isEmpty() {
        return mScrollable.isEmpty();
    }


    @Override
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mScrollable.setOnAdapterClickListener(listener);
    }

    @Override
    public void setOnScrollListener(OnScrollMixListener listener) {
        mScrollable.addOnScrollListener(listener);
    }

    @Override
    public int getItemRealPosition(int position) {
        return mScrollable.getItemRealPosition(position);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mScrollable.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mScrollable.getChildAt(index);
    }

    @Override
    public void onHeaderClick(View v) {
    }

    @Override
    public void onFooterClick(View v) {
    }

    @Override
    public void addEmptyViewIfNonNull() {
        mScrollable.addEmptyViewIfNonNull();
    }

    @Override
    public void setSelection(int position) {
        mScrollable.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mScrollable.smoothScrollToPosition(position);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScrollable.onDestroy();
    }

    @Override
    public int getHeaderViewPosition() {
        return mScrollable.getHeaderViewPosition();
    }

    @Override
    public IScrollable<T> getScrollable() {
        return mScrollable;
    }

    @Override
    public View getScrollableView() {
        return mScrollable.getScrollableView();
    }

    @Override
    public View findViewById(@IdRes int id) {
        return findView(id);
    }

    /**
     * WrapRecyclerView比较特殊, header和footer里面的view在一开始初始化的时候并没有加入到decorView里
     * 所以在使用findView的时候就需要同时也在单独在header和footer里面遍历寻找
     *
     * @param id
     * @return
     */
    @Override
    public <T extends View> T findView(int id) {
        View v = super.findView(id);
        if (v == null) {
            if (getScrollableView() instanceof WrapRecyclerView) {
                v = ((WrapRecyclerView) getScrollableView()).findViewInHeaderById(id);
            }
        }

        if (v == null) {
            if (getScrollableView() instanceof WrapRecyclerView) {
                v = ((WrapRecyclerView) getScrollableView()).findViewInFooterById(id);
            }
        }
        return (T) v;
    }
}
