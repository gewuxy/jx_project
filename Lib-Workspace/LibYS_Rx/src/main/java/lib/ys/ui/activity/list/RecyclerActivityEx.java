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
import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.list.OnRecyclerViewOptListener;
import lib.ys.ui.interfaces.impl.list.RecyclerViewOpt;
import lib.ys.view.recycler.WrapRecyclerView;

/**
 * 下拉刷新 Recycler view
 *
 * @param <T>
 */
abstract public class RecyclerActivityEx<T> extends ActivityEx implements OnRecyclerViewOptListener<T> {

    private RecyclerViewOpt<T> mRecyclerOpt = new RecyclerViewOpt<>(this);

    @Override
    public int getContentViewId() {
        return R.layout.recycler_layout;
    }

    @Override
    public void findViews() {
        mRecyclerOpt.findViews(getDecorView(), getRvResId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @Override
    public void setViews() {
        if (mRecyclerOpt.isAdapterNull()) {
            mRecyclerOpt.createAdapter(createAdapter());
        }
        mRecyclerOpt.setViews(initLayoutManager(), initItemDecoration(), initItemAnimator());
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
    public int getRvResId() {
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
    abstract public MultiRecyclerAdapterEx<T, ? extends RecyclerViewHolderEx> createAdapter();

    @Override
    public MultiRecyclerAdapterEx<T, ? extends RecyclerViewHolderEx> getAdapter() {
        return (MultiRecyclerAdapterEx<T, ? extends RecyclerViewHolderEx>) mRecyclerOpt.getAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }

    @Override
    public void hideFooterView() {
        mRecyclerOpt.hideFooterView();
    }

    @Override
    public void showFooterView() {
        mRecyclerOpt.showFooterView();
    }

    @Override
    public void showHeaderView() {
        mRecyclerOpt.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mRecyclerOpt.hideHeaderView();
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
        mRecyclerOpt.setData(list);
    }

    @Override
    public void addItem(T item) {
        mRecyclerOpt.addItem(item);
    }

    @Override
    public void addItem(int position, T item) {
        mRecyclerOpt.addItem(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mRecyclerOpt.addAll(data);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mRecyclerOpt.addAll(position, item);
    }

    @Override
    public void invalidate() {
        mRecyclerOpt.invalidate();
    }

    @Override
    public void remove(int position) {
        mRecyclerOpt.remove(position);
    }

    @Override
    public void remove(T item) {
        mRecyclerOpt.remove(item);
    }

    @Override
    public void removeAll() {
        mRecyclerOpt.removeAll();
    }

    @Override
    public List<T> getData() {
        return mRecyclerOpt.getData();
    }

    @Override
    public int getCount() {
        return mRecyclerOpt.getCount();
    }

    @Override
    public int getLastItemPosition() {
        return mRecyclerOpt.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mRecyclerOpt.getItem(position);
    }

    @Override
    public boolean isEmpty() {
        return mRecyclerOpt.isEmpty();
    }


    @Override
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mRecyclerOpt.setOnAdapterClickListener(listener);
    }

    @Override
    public void setOnScrollListener(OnScrollMixListener listener) {
        mRecyclerOpt.addOnScrollListener(listener);
    }

    @Override
    public int getItemRealPosition(int position) {
        return mRecyclerOpt.getItemRealPosition(position);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mRecyclerOpt.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mRecyclerOpt.getChildAt(index);
    }

    @Override
    public void onHeaderClick(View v) {
    }

    @Override
    public void onFooterClick(View v) {
    }

    @Override
    public void addEmptyViewIfNoNull() {
        mRecyclerOpt.addEmptyViewIfNonNull();
    }

    @Override
    public void setSelection(int position) {
        mRecyclerOpt.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mRecyclerOpt.smoothScrollToPosition(position);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerOpt.onDestroy();
    }

    @Override
    public int getHeaderViewPosition() {
        return mRecyclerOpt.getHeaderViewPosition();
    }

    protected WrapRecyclerView getRv() {
        return mRecyclerOpt.getRv();
    }

    protected RecyclerViewOpt<T> getRecyclerOpt() {
        return mRecyclerOpt;
    }

    /**
     * WrapRecyclerView比较特殊, header和footer里面的view在一开始初始化的时候并没有加入到decorView里
     * 所以在使用findView的时候就需要同时也在单独在header和footer里面遍历寻找
     *
     * @param id
     * @return
     */
    @Override
    public View findViewById(@IdRes int id) {
        View v = super.findViewById(id);
        if (v == null) {
            v = getRv().findViewInHeaderById(id);
        }
        if (v == null) {
            v = getRv().findViewInFooterById(id);
        }
        return v;
    }
}
