package lib.ys.ui.frag.list;

import android.support.annotation.CallSuper;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.ui.frag.FragEx;
import lib.ys.ui.interfaces.IScrollable;
import lib.ys.ui.interfaces.impl.scrollable.GroupListScrollable;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.scrollable.OnGroupListScrollableListener;
import lib.ys.ui.other.NavBar;


/**
 * 下拉刷新group listView fragment
 */
abstract public class GroupListFragEx<GROUP, CHILD, A extends IGroupAdapter<GROUP, CHILD>>
        extends FragEx
        implements OnGroupListScrollableListener<GROUP, CHILD, A> {

    private GroupListScrollable<GROUP, CHILD, A> mScrollable = new GroupListScrollable<>(this);


    @Override
    public int getContentViewId() {
        return R.layout.group_list_layout;
    }

    @Override
    public int getScrollableViewId() {
        return R.id.group_list;
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

    @Override
    public A getAdapter() {
        return mScrollable.getAdapter();
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
        return mScrollable.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mScrollable.getChildrenCount(groupPosition);
    }

    @Override
    public int getHeaderViewPosition() {
        return mScrollable.getHeaderViewPosition();
    }

    @Override
    public void invalidate() {
        mScrollable.invalidate();
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
        return mScrollable.getItemRealPosition(position);
    }

    @Override
    public boolean isEmpty() {
        return mScrollable.isEmpty();
    }

    @Override
    public void setSelectedGroup(int groupPosition) {
        mScrollable.setSelectedGroup(groupPosition);
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
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public void setData(List<GROUP> data) {
        mScrollable.setData(data);
    }

    @Override
    public void addItem(GROUP item) {
        mScrollable.addItem(item);
    }

    @Override
    public GROUP getGroup(int groupPosition) {
        return mScrollable.getGroup(groupPosition);
    }

    @Override
    public CHILD getChild(int groupPosition, int childPosition) {
        return mScrollable.getChild(groupPosition, childPosition);
    }

    @Override
    public boolean isGroupExpanded(int groupPosition) {
        return mScrollable.isGroupExpanded(groupPosition);
    }

    @Override
    public void expandAllGroup() {
        mScrollable.expandAllGroup();
    }

    @Override
    public void expandGroup(int groupPos) {
        mScrollable.expandGroup(groupPos);
    }

    @Override
    public void collapseAllGroup() {
        mScrollable.collapseAllGroup();
    }

    @Override
    public void collapseGroup(int groupPos) {
        mScrollable.collapseGroup(groupPos);
    }

    @Override
    public void showHeaderView() {
        mScrollable.showHeaderView();
    }

    @Override
    public void hideHeaderView() {
        mScrollable.hideHeaderView();
    }

    @Override
    public void setExpandSingle() {
        mScrollable.setExpandSingle();
    }

    @Override
    public void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener) {
        mScrollable.setOnGroupAdapterClickListener(listener);
    }

    @Override
    public void setOnChildAdapterClickListener(OnChildAdapterClickListener listener) {
        mScrollable.setOnChildAdapterClickListener(listener);
    }

    @Override
    public void setFloatingGroupEnabled(boolean enable) {
        mScrollable.setFloatingGroupEnabled(enable);
    }

    public void getDataFromNet() {
    }

    @Override
    public void addEmptyViewIfNonNull() {
        mScrollable.addEmptyViewIfNonNull();
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return false;
    }

    @Override
    public void addItem(int position, GROUP item) {
        mScrollable.addItem(position, item);
    }

    @Override
    public void addAll(int position, List<GROUP> item) {
        mScrollable.addAll(position, item);
    }

    @Override
    public void addAll(List<GROUP> data) {
        mScrollable.addAll(data);
    }

    @Override
    public void remove(int position) {
        mScrollable.remove(position);
    }

    @Override
    public void remove(GROUP item) {
        mScrollable.remove(item);
    }

    @Override
    public void removeAll() {
        mScrollable.removeAll();
    }

    @Override
    public List<GROUP> getData() {
        return mScrollable.getData();
    }

    @Override
    public int getLastItemPosition() {
        return mScrollable.getLastItemPosition();
    }

    @Override
    public GROUP getItem(int position) {
        return mScrollable.getItem(position);
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
        mScrollable.setOnScrollListener(listener);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mScrollable.getFirstVisiblePosition();
    }

    @Override
    public View getChildAt(int index) {
        return mScrollable.getChildAt(index);
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
        mScrollable.setSelection(position);
    }

    @Override
    public void smoothScrollToPosition(int groupPosition) {
        mScrollable.smoothScrollToPosition(groupPosition);
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
    public IScrollable<GROUP> getScrollable() {
        return mScrollable;
    }

    @Nullable
    @Override
    public <VH extends IViewHolder> VH getGroupCacheVH(@IntRange(from = 0) int groupPosition) {
        if (getAdapter() instanceof MultiGroupAdapterEx) {
            MultiGroupAdapterEx adapter = (MultiGroupAdapterEx) getAdapter();
            return (VH) adapter.getGroupCacheVH(groupPosition);
        } else {
            return null;
        }
    }
}
