package yy.doctor.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;

import lib.jg.jpush.BaseJPushReceiver;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.yy.Notifier;
import lib.yy.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.model.Notice;
import yy.doctor.model.Notice.TNotice;
import yy.doctor.model.NoticeManager;
import yy.doctor.model.NoticeSize;
import yy.doctor.model.jpush.JPushMsg;
import yy.doctor.model.jpush.JPushMsg.TJPushMsg;
import yy.doctor.serv.CommonServ;
import yy.doctor.sp.SpUser;
import yy.doctor.sp.SpUser.SpUserKey;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class JPushReceiver extends BaseJPushReceiver {

    private static String TAG = "JPushReceiver";

    @Override
    protected void onRegistrationId(Context context, String id) {
        YSLog.d(TAG, "onRegistrationId: id = " + id);
        if (!TextUtil.isEmpty(id) && TextUtil.isEmpty(SpUser.inst().getString(SpUserKey.KJPushRegisterId))) {
            Intent intent = new Intent(context, CommonServ.class);
            intent.putExtra(Extra.KType, Extra.KJPushRegisterId)
                    .putExtra(Extra.KData, id);
            context.startService(intent);
            YSLog.d(TAG, "启动绑定极光服务");
        }
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
            notice.put(TNotice.is_read, false);
            notice.setContent(notice.toStoreJson());
            NoticeManager.inst().insert(notice);
            //通知主页面出现小红点
            Notifier.inst().notify(NotifyType.receiver_notice);
            //未读消息数加 1
            NoticeSize.homeInst().add(1);

            Intent intent  = new Intent();
            // type==1 推送的是会议的
            if (jPushMsg.getString(TJPushMsg.msgType).equals("1")) {
                intent.setClass(context, MeetingDetailsActivity.class);
                intent.putExtra(Extra.KData, jPushMsg.getString(TJPushMsg.meetId));
            } else {
                //do nothing
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);//点击后消失
            builder.setSmallIcon(R.mipmap.ic_launcher);//设置通知栏消息标题的头像
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
            builder.setContentText(jPushMsg.getString(TJPushMsg.content));//通知内容
            builder.setContentTitle(jPushMsg.getString(TJPushMsg.title));
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
