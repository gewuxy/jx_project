package jx.csp.receiver;

import android.content.Context;

import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.jpush.JPushMsg;
import jx.csp.model.jpush.JPushMsg.MsgType;
import jx.csp.model.jpush.JPushMsg.TJPushMsg;
import lib.jg.jpush.BaseJPushReceiver;
import lib.jg.jpush.SpJPush;
import lib.ys.YSLog;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;

/**
 * @author Huoxuyu
 * @since 2017/10/20
 */

public class JPushReceiver extends BaseJPushReceiver {

    private static String TAG = "JPushReceiver";

    @Override
    protected void onRegistrationId(Context context, String id) {
        YSLog.d(TAG, "onRegistrationId: id = " + id);
        SpJPush.inst().jPushRegisterId(id);
        YSLog.d(TAG, "保存的RegistrationId = " + SpJPush.inst().registerId());
    }

    //自定义消息
    @Override
    protected void onMessage(Context context, String message) {
        YSLog.d(TAG, "接收到推送下来的自定义消息: message " + message);

        JPushMsg jPushMsg = new JPushMsg();
        jPushMsg.parse(message);

        // type == 3代表绑定邮箱成功
        switch (jPushMsg.getInt(TJPushMsg.msgType)) {
            case MsgType.bind_email_success: {
                Profile.inst().put(TProfile.email, jPushMsg.getString(TJPushMsg.result));
                Profile.inst().saveToSp();
                Notifier.inst().notify(NotifyType.bind_email);
                Notifier.inst().notify(NotifyType.profile_change);
            }
            break;
        }

    }

    //普通消息
    @Override
    protected void onNotification(Context context, String message) {
        //解析数据
        YSLog.d(TAG, " 普通消息 jpush onNotification = " + message);
    }

    //点击事件
    @Override
    protected void onOpenNotification(Context context) {
        YSLog.d(TAG, " 点击事件 jpush onOpenNotification ");
    }

}
