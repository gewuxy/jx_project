package yy.doctor.model.home;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2017/5/17
 */

public interface IHome {

    @IntDef({
            HomeType.meeting,
            HomeType.unit_num,
            HomeType.meeting_folder
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface HomeType {
        int meeting = 0;
        int unit_num = 1;
        int meeting_folder = 2;
    }

    @HomeType
    int getHomeType();

}
