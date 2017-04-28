package yy.doctor.model.unitnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class GroupUnitNum {

    private List<UnitNum> mChildren;
    private String mLetter;

    public GroupUnitNum() {
        mChildren = new ArrayList<>();
    }

    public void add(UnitNum unitnum) {
        mChildren.add(unitnum);
    }

    public UnitNum getChild(int index) {
        // 最大值判断 越界
        if (index < 0 || index > mChildren.size()) {
            return null;
        }
        return mChildren.get(index);
    }

    public int getChildCount() {
        return mChildren.size();
    }

    public String getLetter() {
        return mLetter;
    }

    public void setLetter(String mLetter) {
        this.mLetter = mLetter;
    }

}
