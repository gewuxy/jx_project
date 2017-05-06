package lib.ys.widget.list;

import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.util.UIUtil;
import lib.ys.view.FloatingGroupListView;

/**
 * group list组件
 *
 * @author yuansui
 */
public class GroupListWidget<T> extends ListWidget<T> implements OnGroupClickListener, OnChildClickListener {

    private FloatingGroupListView mLv;
    private IGroupAdapter<T> mAdapter;

    private OnGroupListWidgetListener mListener;

    public GroupListWidget(@NonNull OnGroupListWidgetListener<T> listener) {
        super(listener);
        mListener = listener;
    }

    @CallSuper
    @Override
    public void findViews(View contentView, @IdRes int listId, View header, View footer, View empty) {
        super.findViews(contentView, listId, header, footer, empty);
        mLv = (FloatingGroupListView) contentView.findViewById(listId);
    }

    @Override
    public void setViews() {
        // 不能调用super, 因为adapter类型不同无法设置
        UIUtil.setOverScrollNever(mLv);

        mLv.setAdapter((ExpandableListAdapter) mAdapter);
        mLv.setOnItemClickListener(this);

        if (mListener.enableLongClick()) {
            mLv.setOnItemLongClickListener(this);
        }

        if (!mListener.needDelayAddEmptyView()) {
            addEmptyViewIfNoNull();
        }

        mLv.setOnGroupClickListener(this);
        mLv.setOnChildClickListener(this);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mLv.setOnScrollListener(listener);
    }

    public void setSelectedGroup(int groupPosition) {
        mLv.setSelectedGroup(groupPosition);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return mListener.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return mListener.onGroupClick(parent, v, groupPosition, id);
    }

    @Override
    public void createAdapter(IAdapter<T> adapter) {
        if (adapter instanceof MultiGroupAdapterEx) {
            super.createAdapter(adapter);
            mAdapter = (IGroupAdapter<T>) adapter;
        } else {
            throw new IllegalStateException("must be child of MultiGroupAdapterEx");
        }
    }

    public T getGroup(int groupPosition) {
        return mAdapter.getGroup(groupPosition);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return mAdapter.getChild(groupPosition, childPosition);
    }

    public boolean isGroupExpanded(int groupPosition) {
        return mLv.isGroupExpanded(groupPosition);
    }

    public void expandAllGroup() {
        for (int i = 0; i < mAdapter.getGroupCount(); ++i) {
            mLv.expandGroup(i);
        }
    }

    public void collapseAllGroup() {
        for (int i = 0; i < mAdapter.getGroupCount(); ++i) {
            mLv.collapseGroup(i);
        }
    }

    public void collapseGroup(int groupPos) {
        mLv.collapseGroup(groupPos);
    }

    public void setExpandSingle() {
        mLv.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int position) {
                for (int i = 0; i < mAdapter.getGroupCount(); ++i) {
                    if (i != position) {
                        if (isGroupExpanded(i)) {
                            collapseGroup(i);
                        }
                    }
                }
            }
        });
    }

    public int getGroupCount() {
        return mAdapter.getGroupCount();
    }

    public int getChildrenCount(int groupPosition) {
        return mAdapter.getChildrenCount(groupPosition);
    }

    public void expandGroup(int groupPos) {
        mLv.expandGroup(groupPos);
    }

    public void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener) {
        mAdapter.setOnGroupAdapterClickListener(listener);
    }

    public void setOnChildAdapterClickListener(OnChildAdapterClickListener listener) {
        mAdapter.setOnChildAdapterClickListener(listener);
    }

    public void setFloatingGroupEnabled(boolean enable) {
        mLv.setFloatingGroupEnabled(enable);
    }

    @Override
    public FloatingGroupListView getLv() {
        return mLv;
    }

    @Override
    public IGroupAdapter<T> getAdapter() {
        return (IGroupAdapter<T>) super.getAdapter();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        long pos = mLv.getExpandableListPosition(position);
        int type = ExpandableListView.getPackedPositionType(pos);

        int groupPos = ExpandableListView.getPackedPositionGroup(pos);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int childPos = ExpandableListView.getPackedPositionChild(pos);
            mListener.onChildLongClick(groupPos, childPos);
        } else {
            mListener.onGroupLongClick(groupPos);
        }

        return true;
    }

    public ViewHolderEx getGroupCacheViewHolder(int groupPosition) {
        return getAdapter().getGroupCacheViewHolder(groupPosition);
    }
}
