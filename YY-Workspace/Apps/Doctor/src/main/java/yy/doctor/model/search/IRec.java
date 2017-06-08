package yy.doctor.model.search;

import android.support.annotation.IntDef;

/**
 * @auther : GuoXuan
 * @since : 2017/6/8
 */

public interface IRec {
    @IntDef({
            RecType.meeting,
            RecType.unit_num,
    })
    @interface RecType {
        int meeting = 0;
        int unit_num = 1;
    }

    @RecType
    int getType();
}
