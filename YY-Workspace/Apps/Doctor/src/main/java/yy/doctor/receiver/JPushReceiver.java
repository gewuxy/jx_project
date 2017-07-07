package yy.doctor.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;

import lib.jg.jpush.BaseJPushReceiver;
import lib.jg.jpush.SpJPush;
import lib.ys.YSLog;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.jpush.JPushMsg;
import yy.doctor.model.jpush.JPushMsg.TJPushMsg;
import yy.doctor.model.notice.Notice;
import yy.doctor.model.notice.Notice.TNotice;
import yy.doctor.model.notice.NoticeManager;
import yy.doctor.model.notice.NoticeNum;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class JPushReceiver extends BaseJPushReceiver {

    private static String TAG = "JPushReceiver";
    private static int KMsgMeetingType = 1;

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

        try {
            JPushMsg jPushMsg = new JPushMsg();
            jPushMsg.parse(message);
            YSLog.d(TAG, "type = " + jPushMsg.getString(TJPushMsg.msgType) + "    " + "meetingId = " + jPushMsg.getString(TJPushMsg.meetId));

            //把消息添加进数据库
            Notice notice = new Notice();
            notice.put(TNotice.content, jPushMsg.getString(TJPushMsg.content));
            notice.put(TNotice.from, jPushMsg.getString(TJPushMsg.senderName));
            notice.put(TNotice.time, jPushMsg.getString(TJPushMsg.sendTime));
            notice.put(TNotice.msgType, jPushMsg.getString(TJPushMsg.msgType));
            notice.put(TNotice.meetId, jPushMsg.getString(TJPushMsg.meetId));
            notice.put(TNotice.meetName, jPushMsg.getString(TJPushMsg.meetName));
            notice.put(TNotice.is_read, false);
            notice.setContent(notice.toStoreJson());
            NoticeManager.inst().insert(notice);
            //通知主页面出现小红点
            Notifier.inst().notify(NotifyType.receiver_notice);

            //未读消息数加 1
            NoticeNum.inst().add();

            Intent intent = new Intent();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);//点击后消失
            builder.setSmallIcon(R.mipmap.ic_launcher);//设置通知栏消息标题的头像
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
            builder.setContentText(jPushMsg.getString(TJPushMsg.content));//通知内容
            builder.setContentTitle(jPushMsg.getString(TJPushMsg.title));

            // type==1 推送的是会议的  ==0 是普通消息
            if (jPushMsg.getInt(TJPushMsg.msgType) == KMsgMeetingType) {
                intent.setClass(context, MeetingDetailsActivity.class);
                intent.putExtra(Extra.KData, jPushMsg.getString(TJPushMsg.meetId));
            } else {
                //do nothing
            }

            //利用PendingIntent来包装我们的intent对象,使其延迟跳转
            PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(intentPend);
            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

        } catch (JSONException e) {
            e.printStackTrace();
            YSLog.d(TAG, " jpush msg 解析数据 error = " + e.getMessage());
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
