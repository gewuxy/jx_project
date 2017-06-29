package lib.ys.ui.frag.list;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.ui.frag.FragEx;
import lib.ys.ui.interfaces.impl.list.GroupListOpt;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.list.OnGroupListOptListener;
import lib.ys.ui.other.NavBar;
import lib.ys.view.FloatingGroupListView;


/**
 * 下拉刷新group listView fragment
 */
abstract public class GroupListFragEx<GROUP, CHILD, A extends IGroupAdapter<GROUP, CHILD>>
        extends FragEx
        implements OnGroupListOptListener<GROUP, CHILD, A> {

    private GroupListOpt<GROUP, CHILD, A> mGroupListOpt = new GroupListOpt<>(this);


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
    public void setData(List<GROUP> data) {
        mGroupListOpt.setData(data);
    }

    @Override
    public void addItem(GROUP item) {
        mGroupListOpt.addItem(item);
    }

    @Override
    public GROUP getGroup(int groupPosition) {
        return mGroupListOpt.getGroup(groupPosition);
    }

    @Override
    public CHILD getChild(int groupPosition, int childPosition) {
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
    public void addEmptyViewIfNonNull() {
        mGroupListOpt.addEmptyViewIfNonNull();
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return false;
    }

    @Override
    public void addItem(int position, GROUP item) {
        mGroupListOpt.addItem(position, item);
    }

    @Override
    public void addAll(int position, List<GROUP> item) {
        mGroupListOpt.addAll(position, item);
    }

    @Override
    public void addAll(List<GROUP> data) {
        mGroupListOpt.addAll(data);
    }

    @Override
    public void remove(int position) {
        mGroupListOpt.remove(position);
    }

    @Override
    public void remove(GROUP item) {
        mGroupListOpt.remove(item);
    }

    @Override
    public void removeAll() {
        mGroupListOpt.removeAll();
    }

    @Override
    public List<GROUP> getData() {
        return mGroupListOpt.getData();
    }

    @Override
    public int getLastItemPosition() {
        return mGroupListOpt.getLastItemPosition();
    }

    @Override
    public GROUP getItem(int position) {
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
    public void setOnScrollListener(OnScrollMixListener listener) {
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
    public void changeAlphaByScroll(int height, NavBar navBar) {
        mGroupListOpt.changeAlphaByScroll(height, navBar);
    }

    @Override
    public void setDividerHeight(int height) {
        mGroupListOpt.setDividerHeight(height);
    }

    protected FloatingGroupListView getLv() {
        return mGroupListOpt.getLv();
    }

    protected GroupListOpt<GROUP, CHILD, A> getGroupListOpt() {
        return mGroupListOpt;
    }
}
