package lib.ys.activity.list;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import lib.ys.R;
import lib.ys.activity.ActivityEx;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.ys.widget.list.ListWidget;
import lib.ys.widget.list.OnListWidgetListener;
import lib.ys.widget.list.mix.MixOnScrollListener;

abstract public class ListActivityEx<T> extends ActivityEx implements OnListWidgetListener<T> {

    private ListWidget<T> mListWidget = new ListWidget<T>(this);

    @Override
    public int getContentViewId() {
        return R.layout.list_layout;
    }

    @CallSuper
    @Override
    public void findViews() {
        mListWidget.findViews(getDecorView(), getListViewResId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @CallSuper
    @Override
    public void setViews() {
        if (mListWidget.isAdapterNull()) {
            mListWidget.createAdapter(createAdapter());
        }
        mListWidget.setViews();
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
        return (MultiAdapterEx<T, ?>) mListWidget.getAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }

    @Override
    public void hideFooterView() {
        mListWidget.hideFooterView();
    }

    @Override
    public void showFooterView() {
        mListWidget.showFooterView();
    }

    @Override
    public void showHeaderView() {
        mListWidget.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mListWidget.hideHeaderView();
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
        mListWidget.setData(list);
    }

    @Override
    public void addItem(T item) {
        mListWidget.addItem(item);
    }

    @Override
    public void addItem(int position, T item) {
        mListWidget.addItem(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mListWidget.addAll(data);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mListWidget.addAll(position, item);
    }

    @Override
    public void invalidate() {
        mListWidget.invalidate();
    }

    @Override
    public void remove(int position) {
        mListWidget.remove(position);
    }

    @Override
    public void remove(T item) {
        mListWidget.remove(item);
    }

    @Override
    public void removeAll() {
        mListWidget.removeAll();
    }

    @Override
    public List<T> getData() {
        return mListWidget.getData();
    }

    @Override
    public int getCount() {
        return mListWidget.getCount();
    }

    @Override
    public int getLastItemPosition() {
        return mListWidget.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mListWidget.getItem(position);
    }

    @Override
    public boolean isEmpty() {
        return mListWidget.isEmpty();
    }

    @Override
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mListWidget.setOnAdapterClickListener(listener);
    }

    @Override
    public void setOnScrollListener(MixOnScrollListener listener) {
        mListWidget.setOnScrollListener(listener);
    }

    @Override
    public int getItemRealPosition(int position) {
        return mListWidget.getItemRealPosition(position);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mListWidget.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mListWidget.getChildAt(index);
    }

    @Override
    public void onHeaderClick(View v) {
    }

    @Override
    public void onFooterClick(View v) {
    }

    @Override
    public void addEmptyViewIfNoNull() {
        mListWidget.addEmptyViewIfNoNull();
    }

    @Override
    public void setSelection(int position) {
        mListWidget.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mListWidget.smoothScrollToPosition(position);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListWidget.onDestroy();
    }

    @Override
    public int getHeaderViewPosition() {
        return mListWidget.getHeaderViewPosition();
    }

    @Override
    public void setNavBarAutoAlphaByScroll(int height, NavBar navBar) {
        mListWidget.setNavBarAutoAlphaByScroll(height, navBar);
    }

    @Override
    public void setDividerHeight(int height) {
        mListWidget.setDividerHeight(height);
    }

    protected ListView getLv() {
        return mListWidget.getLv();
    }

    protected ListWidget<T> getListWidget() {
        return mListWidget;
    }
}