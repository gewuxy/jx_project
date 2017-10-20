package jx.csp.model.jpush;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.model.jpush.JPushMsg.TJPushMsg;
import lib.ys.model.EVal;

/**
 * @author Huoxuyu
 * @since 2017/10/20
 */

public class JPushMsg extends EVal<TJPushMsg> {

    @IntDef({
            MsgType.bind_email_success,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MsgType {
        int bind_email_success = 3; // 绑定邮箱成功
    }

    public enum TJPushMsg {
        result,
        msgType,
    }

}
