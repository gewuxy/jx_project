package lib.ys.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther yuansui
 * @since 2017/6/27
 */

abstract public class GroupEx<CHILD> {
    private List<CHILD> mChildren;

    public List<CHILD> getChildren() {
        return mChildren;
    }

    public void setChildren(List<CHILD> children) {
        mChildren = children;
    }

    public void addChild(CHILD child) {
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        mChildren.add(child);
    }

    public boolean removeChild(CHILD child) {
        if (mChildren == null) {
            return false;
        }
        return mChildren.remove(child);
    }

    public boolean removeChild(int position) {
        if (mChildren == null) {
            return false;
        }
        CHILD c = mChildren.remove(position);
        return c != null;
    }

    public CHILD getChildAt(int position) {
        if (mChildren == null || position >= mChildren.size() || position < 0) {
            return null;
        }
        return mChildren.get(position);
    }

    public int getChildrenCount() {
        return mChildren == null ? 0 : mChildren.size();
    }

    public boolean isChildrenEmpty() {
        return getChildrenCount() == 0;
    }
}
