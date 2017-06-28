package lib.ys.model.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther yuansui
 * @since 2017/6/27
 */

abstract public class GroupEx<CHILD> implements IGroup<CHILD>, Serializable {
    private List<CHILD> mChildren;

    @Override
    public List<CHILD> getChildren() {
        return mChildren;
    }

    @Override
    public void setChildren(List<CHILD> children) {
        mChildren = children;
    }

    @Override
    public void addChild(CHILD child) {
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        mChildren.add(child);
    }

    @Override
    public boolean removeChild(CHILD child) {
        if (mChildren == null) {
            return false;
        }
        return mChildren.remove(child);
    }

    @Override
    public boolean removeChild(int position) {
        if (mChildren == null) {
            return false;
        }
        CHILD c = mChildren.remove(position);
        return c != null;
    }

    @Override
    public CHILD getChildAt(int position) {
        if (mChildren == null || position >= mChildren.size() || position < 0) {
            return null;
        }
        return mChildren.get(position);
    }

    @Override
    public int getChildrenCount() {
        return mChildren == null ? 0 : mChildren.size();
    }
}
