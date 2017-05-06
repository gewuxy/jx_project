package lib.ys.frag.list;

import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import lib.ys.adapter.recycler.RecyclerViewHolderEx;
import lib.ys.frag.FragEx;
import lib.ys.view.recycler.WrapRecyclerView;
import lib.ys.widget.list.OnRecyclerWidgetListener;
import lib.ys.widget.list.RecyclerWidget;
import lib.ys.widget.list.mix.MixOnScrollListener;

/**
 * @author yuansui
 */
abstract public class RecyclerFragEx<T> extends FragEx implements OnRecyclerWidgetListener<T> {

    private RecyclerWidget<T> mWidget = new RecyclerWidget<T>(this);

    @Override
    public int getContentViewId() {
        return R.layout.recycler_layout;
    }

    @CallSuper
    @Override
    public void findViews() {
        mWidget.findViews(getDecorView(), getRvResId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @CallSuper
    @Override
    public void setViews() {
        if (mWidget.isAdapterNull()) {
            mWidget.createAdapter(createAdapter());
        }
        mWidget.setViews(initLayoutManager(), initItemDecoration(), initItemAnimator());
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
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
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
        return (MultiRecyclerAdapterEx<T, ? extends RecyclerViewHolderEx>) mWidget.getAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }

    @Override
    public void hideFooterView() {
        mWidget.hideFooterView();
    }

    @Override
    public void showFooterView() {
        mWidget.showFooterView();
    }

    @Override
    public void showHeaderView() {
        mWidget.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mWidget.hideHeaderView();
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
        mWidget.setData(list);
    }

    @Override
    public void addItem(T item) {
        mWidget.addItem(item);
    }

    @Override
    public void addItem(int position, T item) {
        mWidget.addItem(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mWidget.addAll(data);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mWidget.addAll(position, item);
    }

    @Override
    public void invalidate() {
        mWidget.invalidate();
    }

    @Override
    public void remove(int position) {
        mWidget.remove(position);
    }

    @Override
    public void remove(T item) {
        mWidget.remove(item);
    }

    @Override
    public void removeAll() {
        mWidget.removeAll();
    }

    @Override
    public List<T> getData() {
        return mWidget.getData();
    }

    @Override
    public int getCount() {
        return mWidget.getCount();
    }

    @Override
    public int getLastItemPosition() {
        return mWidget.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mWidget.getItem(position);
    }

    @Override
    public boolean isEmpty() {
        return mWidget.isEmpty();
    }


    @Override
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mWidget.setOnAdapterClickListener(listener);
    }

    @Override
    public void setOnScrollListener(MixOnScrollListener listener) {
        mWidget.addOnScrollListener(listener);
    }

    @Override
    public int getItemRealPosition(int position) {
        return mWidget.getItemRealPosition(position);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mWidget.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mWidget.getChildAt(index);
    }

    @Override
    public void onHeaderClick(View v) {
    }

    @Override
    public void onFooterClick(View v) {
    }

    @Override
    public void addEmptyViewIfNoNull() {
        mWidget.addEmptyViewIfNoNull();
    }

    @Override
    public void setSelection(int position) {
        mWidget.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mWidget.smoothScrollToPosition(position);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWidget.onDestroy();
    }

    @Override
    public int getHeaderViewPosition() {
        return mWidget.getHeaderViewPosition();
    }

    protected WrapRecyclerView getRv() {
        return mWidget.getRv();
    }

    protected RecyclerWidget<T> getWidget() {
        return mWidget;
    }

    /**
     * WrapRecyclerView比较特殊, header和footer里面的view在一开始初始化的时候并没有加入到decorView里
     * 所以在使用findView的时候就需要同时也在单独在header和footer里面遍历寻找
     *
     * @param id
     * @return
     */
    @Override
    public View findView(@IdRes int id) {
        View v = super.findView(id);
        if (v == null) {
            v = getRv().findViewInHeaderById(id);
        }
        if (v == null) {
            v = getRv().findViewInFooterById(id);
        }
        return v;
    }
}
