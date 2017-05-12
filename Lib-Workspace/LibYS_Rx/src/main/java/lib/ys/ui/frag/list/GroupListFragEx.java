package lib.ys.ui.frag.list;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.ui.frag.FragEx;
import lib.ys.ui.interfaces.listener.MixOnScrollListener;
import lib.ys.ui.interfaces.listener.list.GroupListOptListener;
import lib.ys.ui.interfaces.opts.list.GroupListOpt;
import lib.ys.ui.other.NavBar;
import lib.ys.view.FloatingGroupListView;


/**
 * 下拉刷新group listView fragment
 */
abstract public class GroupListFragEx<T, A extends IGroupAdapter<T>> extends FragEx implements GroupListOptListener<T, A> {

    private GroupListOpt<T, A> mGroupListOpt = new GroupListOpt<>(this);


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
        mGroupListOpt.findViews(getDecorView(), getListViewResId(), createHeaderView(), createFooterView(), createEmptyView());
    }

    @CallSuper
    @Override
    public void setViews() {
        mGroupListOpt.setViews();
    }

    @Override
    public A getAdapter() {
        return mGroupListOpt.getAdapter();
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
    public int getGroupCount() {
        return mGroupListOpt.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupListOpt.getChildrenCount(groupPosition);
    }

    @Override
    public int getHeaderViewPosition() {
        return mGroupListOpt.getHeaderViewPosition();
    }

    @Override
    public void invalidate() {
        mGroupListOpt.invalidate();
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
        return mGroupListOpt.getItemRealPosition(position);
    }

    @Override
    public boolean isEmpty() {
        return mGroupListOpt.isEmpty();
    }

    @Override
    public void setSelectedGroup(int groupPosition) {
        mGroupListOpt.setSelectedGroup(groupPosition);
    }

    @Override
    public void hideFooterView() {
        mGroupListOpt.hideFooterView();
    }

    @Override
    public void showFooterView() {
        mGroupListOpt.showFooterView();
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
        mGroupListOpt.setData(data);
    }

    @Override
    public void addItem(T item) {
        mGroupListOpt.addItem(item);
    }

    @Override
    public T getGroup(int groupPosition) {
        return mGroupListOpt.getGroup(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroupListOpt.getChild(groupPosition, childPosition);
    }

    @Override
    public boolean isGroupExpanded(int groupPosition) {
        return mGroupListOpt.isGroupExpanded(groupPosition);
    }

    @Override
    public void expandAllGroup() {
        mGroupListOpt.expandAllGroup();
    }

    @Override
    public void expandGroup(int groupPos) {
        mGroupListOpt.expandGroup(groupPos);
    }

    @Override
    public void collapseAllGroup() {
        mGroupListOpt.collapseAllGroup();
    }

    @Override
    public void collapseGroup(int groupPos) {
        mGroupListOpt.collapseGroup(groupPos);
    }

    @Override
    public void showHeaderView() {
        mGroupListOpt.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mGroupListOpt.hideHeaderView();
    }

    @Override
    public void setExpandSingle() {
        mGroupListOpt.setExpandSingle();
    }

    @Override
    public void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener) {
        mGroupListOpt.setOnGroupAdapterClickListener(listener);
    }

    @Override
    public void setOnChildAdapterClickListener(OnChildAdapterClickListener listener) {
        mGroupListOpt.setOnChildAdapterClickListener(listener);
    }

    @Override
    public void setFloatingGroupEnabled(boolean enable) {
        mGroupListOpt.setFloatingGroupEnabled(enable);
    }

    public void getDataFromNet() {
    }

    @Override
    public void addEmptyViewIfNoNull() {
        mGroupListOpt.addEmptyViewIfNoNull();
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return false;
    }

    @Override
    public void addItem(int position, T item) {
        mGroupListOpt.addItem(position, item);
    }

    @Override
    public void addAll(int position, List<T> item) {
        mGroupListOpt.addAll(position, item);
    }

    @Override
    public void addAll(List<T> data) {
        mGroupListOpt.addAll(data);
    }

    @Override
    public void remove(int position) {
        mGroupListOpt.remove(position);
    }

    @Override
    public void remove(T item) {
        mGroupListOpt.remove(item);
    }

    @Override
    public void removeAll() {
        mGroupListOpt.removeAll();
    }

    @Override
    public List<T> getData() {
        return mGroupListOpt.getData();
    }

    @Override
    public int getLastItemPosition() {
        return mGroupListOpt.getLastItemPosition();
    }

    @Override
    public T getItem(int position) {
        return mGroupListOpt.getItem(position);
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
        mGroupListOpt.setOnScrollListener(listener);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mGroupListOpt.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mGroupListOpt.getChildAt(index);
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
        mGroupListOpt.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int groupPosition) {
        mGroupListOpt.smoothScrollToPosition(groupPosition);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGroupListOpt.onDestroy();
    }

    @Override
    public void setNavBarAutoAlphaByScroll(int height, NavBar navBar) {
        mGroupListOpt.setNavBarAutoAlphaByScroll(height, navBar);
    }

    @Override
    public void setDividerHeight(int height) {
        mGroupListOpt.setDividerHeight(height);
    }

    protected FloatingGroupListView getLv() {
        return mGroupListOpt.getLv();
    }

    protected GroupListOpt<T, A> getGroupListOpt() {
        return mGroupListOpt;
    }
}
