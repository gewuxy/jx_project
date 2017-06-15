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
            RecType.hot,
            RecType.margin,
            RecType.more,
    })
    @interface RecType {
        int meeting = 0;
        int unit_num = 1;
        int hot = 2;
        int margin = 3;
        int more = 4;
    }

    @RecType
    int getType();
}
