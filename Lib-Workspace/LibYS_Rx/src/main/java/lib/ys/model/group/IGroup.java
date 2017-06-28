package lib.ys.model.group;

import java.util.List;

/**
 * @auther yuansui
 * @since 2017/6/27
 */
public interface IGroup<CHILD> {

    List<CHILD> getChildren();

    void setChildren(List<CHILD> children);

    void addChild(CHILD child);

    boolean removeChild(CHILD child);

    boolean removeChild(int position);

    CHILD getChildAt(int position);

    int getChildrenCount();
}
