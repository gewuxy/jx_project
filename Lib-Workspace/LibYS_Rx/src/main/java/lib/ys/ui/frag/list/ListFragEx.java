package lib.ys.ui.frag.list;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.frag.FragEx;
import lib.ys.ui.interfaces.IScrollable;
import lib.ys.ui.interfaces.impl.scrollable.ListScrollable;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.scrollable.OnListScrollableListener;
import lib.ys.ui.other.NavBar;


abstract public class ListFragEx<T, A extends IAdapter<T>> extends FragEx implements OnListScrollableListener<T, A> {

    private ListScrollable<T, A> mScrollable = new ListScrollable<>(this);


    @Override
    public int getContentViewId() {
        return R.layout.list_layout;
    }

    @Override
    public int getScrollableViewId() {
        return R.id.list;
    }

    @CallSuper
    @Override
    public void findViews() {
        mScrollable.findViews(getDecorView(), getScrollableViewId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @CallSuper
    @Override
    public void setViews() {
        mScrollable.setViews();
    }

    @Nullable
    @Override
    public View createHeaderView() {
        return null;
    }

    @Nullable
    @Override
    public View createFooterView() {
        return null;
    }

    @Nullable
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
    public void setData(List<T> list) {
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
        mScrollable.setOnScrollListener(listener);
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
    public void onDestroy() {
        super.onDestroy();
        mScrollable.onDestroy();
    }

    @Override
    public int getHeaderViewPosition() {
        return mScrollable.getHeaderViewPosition();
    }

    @Override
    public void changeAlphaByScroll(int height, NavBar navBar) {
        mScrollable.changeAlphaByScroll(height, navBar);
    }

    @Override
    public void setDividerHeight(int height) {
        mScrollable.setDividerHeight(height);
    }

    @Override
    public View getScrollableView() {
        return mScrollable.getScrollableView();
    }

    @Override
    public IScrollable<T> getScrollable() {
        return mScrollable;
    }
}
