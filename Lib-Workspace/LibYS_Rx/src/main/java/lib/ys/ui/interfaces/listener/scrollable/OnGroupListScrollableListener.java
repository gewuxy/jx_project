package lib.ys.ui.interfaces.listener.scrollable;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;

import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.adapter.interfaces.IViewHolder;

/**
 * group children view的所有方法
 *
 * @author yuansui
 */
public interface OnGroupListScrollableListener<GROUP, CHILD, A extends IGroupAdapter<GROUP, CHILD>>
        extends OnListScrollableListener<GROUP, A> {

    void setSelectedGroup(int groupPosition);

    int getGroupCount();

    int getChildrenCount(int groupPosition);

    GROUP getGroup(int groupPosition);

    CHILD getChild(int groupPosition, int childPosition);

    boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id);

    boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id);

    /**
     * group是否展开
     *
     * @param groupPosition
     * @return
     */
    boolean isGroupExpanded(int groupPosition);

    /**
     * 展开所有的group项
     */
    void expandAllGroup();

    /**
     * 展开单一group
     *
     * @param groupPos
     */
    void expandGroup(int groupPos);

    /**
     * 关闭所有展开项
     */
    void collapseAllGroup();

    /**
     * 关闭单一group
     *
     * @param groupPos
     */
    void collapseGroup(int groupPos);

    /**
     * 加入单一展开模式, 最多只能有一项展开
     */
    void setExpandSingle();

    /**
     * 外部使用group点击监听
     *
     * @param listener
     */
    void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener);

    /**
     * 外部使用child点击监听
     *
     * @param listener
     */
    void setOnChildAdapterClickListener(OnChildAdapterClickListener listener);

    /**
     * 设置group是否悬停
     *
     * @param enable
     */
    void setFloatingGroupEnabled(boolean enable);

    /**
     * 长按groupItem
     *
     * @param groupPosition
     */
    void onGroupLongClick(int groupPosition);

    /**
     * 长按childItem
     *
     * @param groupPosition
     * @param childPosition
     */
    void onChildLongClick(int groupPosition, int childPosition);

    @Nullable
    <VH extends IViewHolder> VH getGroupCacheVH(@IntRange(from = 0) int groupPosition);
}
