package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * FIXME: 其他状态呢???
 */
@IntDef({
        MsgType.bind_email_success,
})
@Retention(RetentionPolicy.SOURCE)
public @interface MsgType {
    int bind_email_success = 3; // 绑定邮箱成功
}