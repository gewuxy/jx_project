package lib.ys.ui.activity.list;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.list.OnListOptListener;
import lib.ys.ui.interfaces.impl.list.ListOpt;
import lib.ys.ui.other.NavBar;

abstract public class ListActivityEx<T, A extends IAdapter<T>>
        extends ActivityEx
        implements OnListOptListener<T, A> {

    private ListOpt<T, A> mListOpt = new ListOpt<>(this);


    @Override
    public int getContentViewId() {
        return R.layout.list_layout;
    }

    @CallSuper
    @Override
    public void findViews() {
        mListOpt.findViews(getDecorView(), getListViewResId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @CallSuper
    @Override
    public void setViews() {
        mListOpt.setViews();
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
    public A getAdapter() {
        return mListOpt.getAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }

    @Override
    public void hideFooterView() {
        mListOpt.hideFooterView();
    }

    @Override
    public void showFooterView() {
        mListOpt.showFooterView();
    }

    @Override
    public void showHeaderView() {
        mListOpt.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mListOpt.hideHeaderView();
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
        mListOpt.setData(list);
    }

    @Override
    public void addItem(T item) {
        mListOpt.addItem(item);
    }

    @Override
    public void addItem(int position, T item) {
        mListOpt.addItem(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mListOpt.addAll(data);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mListOpt.addAll(position, item);
    }

    @Override
    public void invalidate() {
        mListOpt.invalidate();
    }

    @Override
    public void remove(int position) {
        mListOpt.remove(position);
    }

    @Override
    public void remove(T item) {
        mListOpt.remove(item);
    }

    @Override
    public void removeAll() {
        mListOpt.removeAll();
    }

    @Override
    public List<T> getData() {
        return mListOpt.getData();
    }

    @Override
    public int getCount() {
        return mListOpt.getCount();
    }

    @Override
    public int getLastItemPosition() {
        return mListOpt.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mListOpt.getItem(position);
    }

    @Override
    public boolean isEmpty() {
        return mListOpt.isEmpty();
    }

    @Override
    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mListOpt.setOnAdapterClickListener(listener);
    }

    @Override
    public void setOnScrollListener(OnScrollMixListener listener) {
        mListOpt.setOnScrollListener(listener);
    }

    @Override
    public int getItemRealPosition(int position) {
        return mListOpt.getItemRealPosition(position);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mListOpt.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mListOpt.getChildAt(index);
    }

    @Override
    public void onHeaderClick(View v) {
    }

    @Override
    public void onFooterClick(View v) {
    }

    @Override
    public void addEmptyViewIfNoNull() {
        mListOpt.addEmptyViewIfNonNull();
    }

    @Override
    public void setSelection(int position) {
        mListOpt.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mListOpt.smoothScrollToPosition(position);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListOpt.onDestroy();
    }

    @Override
    public int getHeaderViewPosition() {
        return mListOpt.getHeaderViewPosition();
    }

    @Override
    public void setNavBarAutoAlphaByScroll(int height, NavBar navBar) {
        mListOpt.setNavBarAutoAlphaByScroll(height, navBar);
    }

    @Override
    public void setDividerHeight(int height) {
        mListOpt.setDividerHeight(height);
    }

    protected ListView getLv() {
        return mListOpt.getLv();
    }

    protected ListOpt<T, A> getListOpt() {
        return mListOpt;
    }
}