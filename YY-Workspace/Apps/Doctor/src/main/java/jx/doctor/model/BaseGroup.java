package jx.doctor.model;

import lib.ys.model.group.GroupEx;


/**
 * @author : GuoXuan
 * @since : 2017/4/29
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
