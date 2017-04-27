package yy.doctor.model.hospital;

import java.util.ArrayList;
import java.util.List;


/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class GroupHospital  {

    private List<Hospital> mChildren;
    private String mInitial;

    public GroupHospital() {
        mChildren = new ArrayList<>();
    }

    public void add(Hospital hospital) {
        mChildren.add(hospital);
    }

    public Hospital getChild(int index) {
        // 最大值判断 越界
        if (index >= mChildren.size() || index < 0) {
            return null;
        }
        return mChildren.get(index);
    }

    public String getInitial() {
        return mInitial;
    }

    public void setInitial(String initial) {
        mInitial = initial;
    }

    public int getChildCount() {
        return mChildren.size();
    }
}
