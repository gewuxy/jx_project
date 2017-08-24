package yy.doctor.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import lib.jg.jpush.BaseJPushReceiver;
import lib.jg.jpush.SpJPush;
import lib.ys.YSLog;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.jpush.JPushMsg;
import yy.doctor.model.jpush.JPushMsg.MsgType;
import yy.doctor.model.jpush.JPushMsg.TJPushMsg;
import yy.doctor.model.notice.Notice;
import yy.doctor.model.notice.Notice.TNotice;
import yy.doctor.model.notice.NoticeManager;
import yy.doctor.model.notice.NoticeNum;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivityRouter;

/**
 * @author CaiXiang
 * @since 2017/5/26
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
        YSLog.d(TAG, "type = " + jPushMsg.getString(TJPushMsg.msgType));

        // type==1 推送的是会议的  ==0 是普通消息  == 2 代表象数发生改变
        if (jPushMsg.getInt(TJPushMsg.msgType) == MsgType.common || jPushMsg.getInt(TJPushMsg.msgType) == MsgType.meeting) {

            //把消息添加进数据库
            Notice notice = new Notice();
            notice.put(TNotice.content, jPushMsg.getString(TJPushMsg.content));
            notice.put(TNotice.from, jPushMsg.getString(TJPushMsg.senderName));
            notice.put(TNotice.time, jPushMsg.getString(TJPushMsg.sendTime));
            notice.put(TNotice.msgType, jPushMsg.getString(TJPushMsg.msgType));
            notice.put(TNotice.meetId, jPushMsg.getString(TJPushMsg.meetId));
            notice.put(TNotice.meetName, jPushMsg.getString(TJPushMsg.meetName));
            notice.put(TNotice.is_read, false);
            notice.setContent(notice.toJson());
            NoticeManager.inst().insert(notice);
            //通知主页面出现小红点
            Notifier.inst().notify(NotifyType.receiver_notice);
            //未读消息数加 1
            NoticeNum.inst().add();

            Intent intent = null;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);//点击后消失
            builder.setSmallIcon(R.mipmap.ic_launcher);//设置通知栏消息标题的头像
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
            builder.setContentText(jPushMsg.getString(TJPushMsg.content));//通知内容
            builder.setContentTitle(jPushMsg.getString(TJPushMsg.title));

            if (jPushMsg.getInt(TJPushMsg.msgType) == MsgType.meeting) {
                intent = MeetingDetailsActivityRouter.newIntent(context, jPushMsg.getString(TJPushMsg.meetId), jPushMsg.getString(TJPushMsg.meetName));
            } else {
                intent = new Intent();
            }

            //利用PendingIntent来包装我们的intent对象,使其延迟跳转
            PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(intentPend);
            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

        } else if (jPushMsg.getInt(TJPushMsg.msgType) == MsgType.epn_change) {
            Profile.inst().put(TProfile.credits, jPushMsg.getString(TJPushMsg.result));
            Profile.inst().saveToSp();
            Notifier.inst().notify(NotifyType.profile_change);
        } else if (jPushMsg.getInt(TJPushMsg.msgType) == MsgType.bind_email_success) {
            Profile.inst().put(TProfile.username, jPushMsg.getString(TJPushMsg.result));
            Profile.inst().saveToSp();
            Notifier.inst().notify(NotifyType.bind_email);
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
