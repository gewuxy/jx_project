package lib.ys.adapter.interfaces;

import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;
import lib.ys.adapter.ViewHolderEx;

/**
 * @author yuansui
 */
public interface IGroupAdapter<T> extends IAdapter<T> {
    T getGroup(int groupPosition);

    Object getChild(int groupPosition, int childPosition);

    int getGroupCount();

    int getChildrenCount(int groupPosition);

    void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener);

    void setOnChildAdapterClickListener(OnChildAdapterClickListener listener);

    /**
     * 通过位置查找group viewHolder
     *
     * @param groupPosition
     * @return 没有则返回null
     */
    ViewHolderEx getGroupCacheViewHolder(int groupPosition);
}
