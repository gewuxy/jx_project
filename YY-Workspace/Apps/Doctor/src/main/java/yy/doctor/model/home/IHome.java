package yy.doctor.model.home;

import android.support.annotation.IntDef;

/**
 * @author CaiXiang
 * @since 2017/5/17
 */

public interface IHome {

    @IntDef({
            HomeType.meeting,
            HomeType.unit_num,
    })
    @interface HomeType {
        int meeting = 0;
        int unit_num = 1;
    }

    @HomeType
    int getType();
}
