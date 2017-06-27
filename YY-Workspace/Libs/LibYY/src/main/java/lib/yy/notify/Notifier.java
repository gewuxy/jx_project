package lib.yy.notify;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.NotifierEx;
import lib.yy.notify.Notifier.OnNotify;

/**
 * 全局通知, 支持任意行为和数据
 *
 * @author yuansui
 */

public class Notifier extends NotifierEx<OnNotify> {

    @IntDef({
            NotifyType.login,
            NotifyType.logout,
            NotifyType.finish,
            NotifyType.section_change,
            NotifyType.profile_change,
            NotifyType.unit_num_attention_change,
            NotifyType.receiver_notice,
            NotifyType.read_all_notice,
            NotifyType.token_out_of_date,
            NotifyType.cancel_attention,
            NotifyType.study,
            NotifyType.cancel_collection_meeting,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotifyType {

        /**
         * 登录成功
         * data = null
         */
        int login = 0;
        /**
         * 登出
         */
        int logout = 1;
        /**
         * 关闭其他页面
         */
        int finish = 2;
        /**
         * 科室改变
         */
        int section_change = 3;

        /**
         * 个人资料更新
         */
        int profile_change = 4;

        /**
         * 单位号关注改变
         */
        int unit_num_attention_change = 5;

        /**
         * 收到通知
         */
        int receiver_notice = 6;

        /**
         * 通知都已经读完
         */
        int read_all_notice = 7;

        /**
         * token过期
         */
        int token_out_of_date = 8;

        /**
         * 取消关注  单位号
         */
        int cancel_attention = 9;

        /**
         * 学习
         */
        int study = 10;

        /**
         * 取消 收藏会议
         */
        int cancel_collection_meeting = 11;
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
