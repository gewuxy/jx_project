package lib.live;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther : GuoXuan
 * @since : 2017/12/11
 */
public interface IProvider {

    @IntDef({
            UserType.anchor,
            UserType.audience,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface UserType {
        int anchor = 0; // 主播
        int audience = 1; // 观众
    }

    void loginRoom();

    void logoutRoom();

    void onDestroy();

}
