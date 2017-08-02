package yy.doctor.model.meet;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther : GuoXuan
 * @since : 2017/8/1
 */
public interface IMeet {

    @IntDef({
            MeetType.meet,
            MeetType.folder,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface MeetType {
        int meet = 0;
        int folder = 1;
    }

    @MeetType
    int getMeetType();
}
