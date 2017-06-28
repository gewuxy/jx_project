package lib.ys.adapter;

import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.model.group.IGroup;

abstract public class GroupAdapterEx<GROUP extends IGroup<CHILD>, CHILD, VH extends IViewHolder>
        extends MultiGroupAdapterEx<GROUP, CHILD, VH> {

    @Override
    public final int getChildType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildTypeCount() {
        return 1;
    }

    @Override
    public final int getGroupType(int groupPosition) {
        return 0;
    }

    @Override
    public final int getGroupTypeCount() {
        return 1;
    }

    @Override
    public final int getGroupConvertViewResId(int groupType) {
        return getGroupConvertViewResId();
    }

    @Override
    protected final void initGroupView(int groupPosition, boolean isExpanded, VH holder, int groupType) {
        initGroupView(groupPosition, isExpanded, holder);
    }

    @Override
    protected final void refreshGroupView(int groupPosition, boolean isExpanded, VH holder, int groupType) {
        refreshGroupView(groupPosition, isExpanded, holder);
    }

    @Override
    public final int getChildConvertViewResId(int childType) {
        return getChildConvertViewResId();
    }

    @Override
    protected void initChildView(int groupPosition, int childPosition, boolean isLastChild, VH holder, int childType) {
        initChildView(groupPosition, childPosition, isLastChild, holder);
    }

    @Override
    protected void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, VH holder, int childType) {
        refreshChildView(groupPosition, childPosition, isLastChild, holder);
    }

    abstract public int getGroupConvertViewResId();

    protected void initGroupView(int groupPosition, boolean isExpanded, VH holder) {
    }

    abstract public void refreshGroupView(int groupPosition, boolean isExpanded, VH holder);

    abstract public int getChildConvertViewResId();

    protected void initChildView(int groupPosition, int childPosition, boolean isLastChild, VH holder) {
    }

    abstract public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, VH holder);

}
