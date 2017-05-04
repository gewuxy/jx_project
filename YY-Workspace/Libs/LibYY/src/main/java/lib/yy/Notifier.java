package lib.yy;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.NotifierEx;
import lib.yy.Notifier.OnNotify;

/**
 * 全局通知, 支持任意行为和数据
 *
 * @author yuansui
 */

public class Notifier extends NotifierEx<OnNotify> {

    @IntDef({
            NotifyType.login,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotifyType {

        /**
         * 登录成功
         * data = null
         */
        int login = 0;
    }

    public interface OnNotify {
        void onNotify(@NotifyType int type, Object data);
    }

    private static Notifier mInst = null;

    public static Notifier inst() {
        if (mInst == null) {
            synchronized (Notifier.class) {
                if (mInst == null) {
                    mInst = new Notifier();
                }
            }
        }
        return mInst;
    }

    @Override
    protected void callback(OnNotify o, @NotifyType int type, Object data) {
        o.onNotify(type, data);
    }
}
