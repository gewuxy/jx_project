package yaya.csp.model;

import lib.ys.model.group.GroupEx;


/**
 * @auther HuoXuYu
 * @since 2017/9/21
 */

public class BaseGroup<CHILD> extends GroupEx<CHILD> {

    private String mTag;

    public String getTag() {
        return mTag;
    }

    public void setTag(String t) {
        mTag = t;
    }
}
