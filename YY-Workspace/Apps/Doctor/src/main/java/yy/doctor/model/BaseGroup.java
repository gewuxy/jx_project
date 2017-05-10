package yy.doctor.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author : GuoXuan
 * @since : 2017/4/29
 */

public class BaseGroup<T> {

    private List<T> mChildren;
    private String mTag;

    public BaseGroup() {
        mChildren = new ArrayList<>();
    }

    public void add(T t) {
        mChildren.add(t);
    }

    public T getChild(int index) {
        // 最大值判断 越界
        if (index >= mChildren.size() || index < 0) {
            return null;
        }
        return mChildren.get(index);
    }

    public int getChildCount() {
        return mChildren.size();
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String t) {
        mTag = t;
    }
}
