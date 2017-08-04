package yy.doctor.model.home;

import java.util.List;

/**
 * @author CaiXiang
 * @since 2017/5/17
 */

public class RecUnitNums implements IHome {

    @Override
    public int getHomeType() {
        return HomeType.unit_num;
    }

    private List<RecUnitNum> mData;

    public List<RecUnitNum> getData() {
        return mData;
    }

    public void setData(List<RecUnitNum> data) {
        mData = data;
    }

}
