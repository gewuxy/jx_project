package lib.ys.ui.interfaces.impl.scrollable;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.ui.interfaces.listener.scrollable.OnGroupListScrollableListener;
import lib.ys.util.UIUtil;
import lib.ys.view.FloatingGroupListView;

/**
 * group list操作
 *
 * @author yuansui
 */
public class GroupListScrollable<GROUP, CHILD, A extends IGroupAdapter<GROUP, CHILD>>
        extends ListScrollable<GROUP, A>
        implements OnGroupClickListener, OnChildClickListener {

    private FloatingGroupListView mGroupLv;
    private A mAdapter;

    private OnGroupListScrollableListener mListener;

    public GroupListScrollable(@NonNull OnGroupListScrollableListener<GROUP, CHILD, A> l) {
        super(l);
        mListener = l;
    }

    @Override
    public <VIEW extends View> VIEW getScrollableView() {
        return (VIEW) mGroupLv;
    }

    @Override
    public void findViews(@NonNull View contentView, @IdRes int scrollableId, @Nullable View header, @Nullable View footer, @Nullable View empty) {
        super.findViews(contentView, scrollableId, header, footer, empty);
        mGroupLv = (FloatingGroupListView) contentView.findViewById(scrollableId);
    }

    @Override
    public void setViews() {
        createAdapter();

        // 不能调用super, 因为adapter类型不同无法进行相同的设置
        UIUtil.setOverScrollNever(mGroupLv);

        mGroupLv.setAdapter((ExpandableListAdapter) mAdapter);
        mGroupLv.setOnItemClickListener(this);

        if (mListener.enableLongClick()) {
            mGroupLv.setOnItemLongClickListener(this);
        }

        if (!mListener.needDelayAddEmptyView()) {
            addEmptyViewIfNonNull();
        }

        mGroupLv.setOnGroupClickListener(this);
        mGroupLv.setOnChildClickListener(this);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mGroupLv.setOnScrollListener(listener);
    }

    public void setSelectedGroup(int groupPosition) {
        mGroupLv.setSelectedGroup(groupPosition);
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
    public void createAdapter() {
        super.createAdapter();
        // 接一下, 减少getAdapter()的开销
        mAdapter = getAdapter();
    }

    public GROUP getGroup(int groupPosition) {
        return mAdapter.getGroup(groupPosition);
    }

    public CHILD getChild(int groupPosition, int childPosition) {
        return mAdapter.getChild(groupPosition, childPosition);
    }

    public boolean isGroupExpanded(int groupPosition) {
        return mGroupLv.isGroupExpanded(groupPosition);
    }

    public void expandAllGroup() {
        for (int i = 0; i < mAdapter.getGroupCount(); ++i) {
            mGroupLv.expandGroup(i);
        }
    }

    public void collapseAllGroup() {
        for (int i = 0; i < mAdapter.getGroupCount(); ++i) {
            mGroupLv.collapseGroup(i);
        }
    }

    public void collapseGroup(int groupPos) {
        mGroupLv.collapseGroup(groupPos);
    }

    public void setExpandSingle() {
        mGroupLv.setOnGroupExpandListener(position -> {
            for (int i = 0; i < mAdapter.getGroupCount(); ++i) {
                if (i != position) {
                    if (isGroupExpanded(i)) {
                        collapseGroup(i);
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
        mGroupLv.expandGroup(groupPos);
    }

    public void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener) {
        mAdapter.setOnGroupAdapterClickListener(listener);
    }

    public void setOnChildAdapterClickListener(OnChildAdapterClickListener listener) {
        mAdapter.setOnChildAdapterClickListener(listener);
    }

    public void setFloatingGroupEnabled(boolean enable) {
        mGroupLv.setFloatingGroupEnabled(enable);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        long pos = mGroupLv.getExpandableListPosition(position);
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
}
