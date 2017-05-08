package lib.ys.ui.activity.list;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import lib.ys.R;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ui.other.NavBar;
import lib.ys.ui.interfaces.opts.impl.list.ListOptImpl;
import lib.ys.ui.interfaces.opts.list.ListOpt;
import lib.ys.ui.interfaces.MixOnScrollListener;

abstract public class ListActivityEx<T> extends ActivityEx implements ListOpt<T> {

    private ListOptImpl<T> mListOptImpl = new ListOptImpl<T>(this);

    @Override
    public int getContentViewId() {
        return R.layout.list_layout;
    }

    @CallSuper
    @Override
    public void findViews() {
        mListOptImpl.findViews(getDecorView(), getListViewResId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @CallSuper
    @Override
    public void setViews() {
        if (mListOptImpl.isAdapterNull()) {
            mListOptImpl.createAdapter(createAdapter());
        }
        mListOptImpl.setViews();
    }

    @Override
    public int getListViewResId() {
        return R.id.list;
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
    abstract public MultiAdapterEx<T, ? extends ViewHolderEx> createAdapter();

    @Override
    public MultiAdapterEx<T, ? extends ViewHolderEx> getAdapter() {
        return (MultiAdapterEx<T, ?>) mListOptImpl.getAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }

    @Override
    public void hideFooterView() {
        mListOptImpl.hideFooterView();
    }

    @Override
    public void showFooterView() {
        mListOptImpl.showFooterView();
    }

    @Override
    public void showHeaderView() {
        mListOptImpl.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mListOptImpl.hideHeaderView();
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
        mListOptImpl.setData(list);
    }

    @Override
    public void addItem(T item) {
        mListOptImpl.addItem(item);
    }

    @Override
    public void addItem(int position, T item) {
        mListOptImpl.addItem(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mListOptImpl.addAll(data);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mListOptImpl.addAll(position, item);
    }

    @Override
    public void invalidate() {
        mListOptImpl.invalidate();
    }

    @Override
    public void remove(int position) {
        mListOptImpl.remove(position);
    }

    @Override
    public void remove(T item) {
        mListOptImpl.remove(item);
    }

    @Override
    public void removeAll() {
        mListOptImpl.removeAll();
    }

    @Override
    public List<T> getData() {
        return mListOptImpl.getData();
    }

    @Override
    public int getCount() {
        return mListOptImpl.getCount();
    }

    @Override
    public int getLastItemPosition() {
        return mListOptImpl.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mListOptImpl.getItem(position);
    }

    @Override
    public boolean isEmpty() {
        return mListOptImpl.isEmpty();
    }

    @Override
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mListOptImpl.setOnAdapterClickListener(listener);
    }

    @Override
    public void setOnScrollListener(MixOnScrollListener listener) {
        mListOptImpl.setOnScrollListener(listener);
    }

    @Override
    public int getItemRealPosition(int position) {
        return mListOptImpl.getItemRealPosition(position);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mListOptImpl.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mListOptImpl.getChildAt(index);
    }

    @Override
    public void onHeaderClick(View v) {
    }

    @Override
    public void onFooterClick(View v) {
    }

    @Override
    public void addEmptyViewIfNoNull() {
        mListOptImpl.addEmptyViewIfNoNull();
    }

    @Override
    public void setSelection(int position) {
        mListOptImpl.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mListOptImpl.smoothScrollToPosition(position);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListOptImpl.onDestroy();
    }

    @Override
    public int getHeaderViewPosition() {
        return mListOptImpl.getHeaderViewPosition();
    }

    @Override
    public void setNavBarAutoAlphaByScroll(int height, NavBar navBar) {
        mListOptImpl.setNavBarAutoAlphaByScroll(height, navBar);
    }

    @Override
    public void setDividerHeight(int height) {
        mListOptImpl.setDividerHeight(height);
    }

    protected ListView getLv() {
        return mListOptImpl.getLv();
    }

    protected ListOptImpl<T> getListWidget() {
        return mListOptImpl;
    }
}