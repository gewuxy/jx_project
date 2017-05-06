package lib.ys.activity.list;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.List;

import lib.ys.R;
import lib.ys.activity.ActivityEx;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.ys.view.FloatingGroupListView;
import lib.ys.widget.list.GroupListWidget;
import lib.ys.widget.list.OnGroupListWidgetListener;
import lib.ys.widget.list.mix.MixOnScrollListener;

abstract public class GroupListActivityEx<T> extends ActivityEx implements OnGroupListWidgetListener<T> {

    private GroupListWidget<T> mListWidget = new GroupListWidget<T>(this);

    @Override
    public int getContentViewId() {
        return R.layout.group_list_layout;
    }

    @Override
    public int getListViewResId() {
        return R.id.group_list;
    }

    @CallSuper
    @Override
    public void findViews() {
        mListWidget.findViews(getDecorView(), getListViewResId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @Override
    public void setViews() {
        if (mListWidget.isAdapterNull()) {
            mListWidget.createAdapter(createAdapter());
        }
        mListWidget.setViews();
    }

    @Override
    public MultiGroupAdapterEx<T, ? extends ViewHolderEx> getAdapter() {
        return (MultiGroupAdapterEx<T, ? extends ViewHolderEx>) mListWidget.getAdapter();
    }

    @Override
    abstract public MultiGroupAdapterEx<T, ? extends ViewHolderEx> createAdapter();

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
    public int getGroupCount() {
        return mListWidget.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListWidget.getChildrenCount(groupPosition);
    }

    @Override
    public int getHeaderViewPosition() {
        return mListWidget.getHeaderViewPosition();
    }

    @Override
    public void invalidate() {
        mListWidget.invalidate();
    }

    /**
     * 无效
     */
    @Override
    @Deprecated
    public int getCount() {
        return 0;
    }

    @Override
    public int getItemRealPosition(int position) {
        return mListWidget.getItemRealPosition(position);
    }

    @Override
    public boolean isEmpty() {
        return mListWidget.isEmpty();
    }

    @Override
    public void setSelectedGroup(int groupPosition) {
        mListWidget.setSelectedGroup(groupPosition);
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
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public void setData(List<T> data) {
        mListWidget.setData(data);
    }

    @Override
    public void addItem(T item) {
        mListWidget.addItem(item);
    }

    @Override
    public T getGroup(int groupPosition) {
        return mListWidget.getGroup(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListWidget.getChild(groupPosition, childPosition);
    }

    @Override
    public boolean isGroupExpanded(int groupPosition) {
        return mListWidget.isGroupExpanded(groupPosition);
    }

    @Override
    public void expandAllGroup() {
        mListWidget.expandAllGroup();
    }

    @Override
    public void expandGroup(int groupPos) {
        mListWidget.expandGroup(groupPos);
    }

    @Override
    public void collapseAllGroup() {
        mListWidget.collapseAllGroup();
    }

    @Override
    public void collapseGroup(int groupPos) {
        mListWidget.collapseGroup(groupPos);
    }

    @Override
    public void showHeaderView() {
        mListWidget.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mListWidget.hideHeaderView();
    }

    @Override
    public void setExpandSingle() {
        mListWidget.setExpandSingle();
    }

    @Override
    public void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener) {
        mListWidget.setOnGroupAdapterClickListener(listener);
    }

    @Override
    public void setOnChildAdapterClickListener(OnChildAdapterClickListener listener) {
        mListWidget.setOnChildAdapterClickListener(listener);
    }

    @Override
    public void setFloatingGroupEnabled(boolean enable) {
        mListWidget.setFloatingGroupEnabled(enable);
    }

    public void getDataFromNet() {
    }

    @Override
    public void addEmptyViewIfNoNull() {
        mListWidget.addEmptyViewIfNoNull();
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return false;
    }

    @Override
    public void addItem(int position, T item) {
        mListWidget.addItem(position, item);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mListWidget.addAll(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mListWidget.addAll(data);
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
    public int getLastItemPosition() {
        return mListWidget.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mListWidget.getItem(position);
    }

    /**
     * group list无效
     */
    @Override
    @Deprecated
    public final void setOnAdapterClickListener(OnAdapterClickListener listener) {
    }

    @Override
    public void setOnScrollListener(MixOnScrollListener listener) {
        mListWidget.setOnScrollListener(listener);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mListWidget.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mListWidget.getChildAt(index);
    }

    /**
     * 无效
     */
    @Override
    @Deprecated
    public final void onItemClick(View v, int position) {
    }

    /**
     * 无效
     */
    @Override
    @Deprecated
    public final void onItemLongClick(View v, int position) {
    }

    @Override
    public void onGroupLongClick(int groupPosition) {
    }

    @Override
    public void onChildLongClick(int groupPosition, int childPosition) {
    }

    @Override
    public void onHeaderClick(View v) {
    }

    @Override
    public void onFooterClick(View v) {
    }

    @Override
    public boolean enableLongClick() {
        return false;
    }

    @Override
    @Deprecated
    public void setSelection(int position) {
        mListWidget.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int groupPosition) {
        mListWidget.smoothScrollToPosition(groupPosition);
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
    public void setNavBarAutoAlphaByScroll(int height, NavBar navBar) {
        mListWidget.setNavBarAutoAlphaByScroll(height, navBar);
    }

    @Override
    public void setDividerHeight(int height) {
        mListWidget.setDividerHeight(height);
    }

    @Override
    public ViewHolderEx getGroupCacheViewHolder(int groupPosition) {
        return mListWidget.getGroupCacheViewHolder(groupPosition);
    }

    protected FloatingGroupListView getLv() {
        return mListWidget.getLv();
    }

    protected GroupListWidget<T> getListWidget() {
        return mListWidget;
    }
}
