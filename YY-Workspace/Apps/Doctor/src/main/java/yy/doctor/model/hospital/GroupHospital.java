package yy.doctor.model.hospital;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class GroupHospital {
    private List<Hospital> mChildren;

    public GroupHospital() {
        mChildren = new ArrayList<>();
    }

    public void add(Hospital h) {
        mChildren.add(h);
    }

    public Hospital getChild(int index) {
        // 最大值判断 越界
        return mChildren.get(index);
    }

    public int getChildCount() {
        return mChildren.size();
    }
}
