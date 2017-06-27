package lib.ys.adapter.interfaces;

import lib.ys.adapter.MultiGroupAdapterEx.OnChildAdapterClickListener;
import lib.ys.adapter.MultiGroupAdapterEx.OnGroupAdapterClickListener;

/**
 * @author yuansui
 */
public interface IGroupAdapter<GROUP, CHILD> extends IAdapter<GROUP> {
    GROUP getGroup(int groupPosition);

    CHILD getChild(int groupPosition, int childPosition);

    int getGroupCount();

    int getChildrenCount(int groupPosition);

    void setOnGroupAdapterClickListener(OnGroupAdapterClickListener listener);

    void setOnChildAdapterClickListener(OnChildAdapterClickListener listener);
}
