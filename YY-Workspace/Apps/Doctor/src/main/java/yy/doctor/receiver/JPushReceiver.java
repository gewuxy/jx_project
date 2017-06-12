package yy.doctor.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;

import lib.jg.jpush.BaseJPushReceiver;
import lib.ys.LogMgr;
import lib.ys.util.TextUtil;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
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
        LogMgr.d(TAG, "onRegistrationId: id = " + id);
        if (!TextUtil.isEmpty(id) && TextUtil.isEmpty(SpUser.inst().getString(SpUserKey.KJPushRegisterId))) {
            Intent intent = new Intent(context, CommonServ.class);
            intent.putExtra(Extra.KType, Extra.KJPushRegisterId)
                    .putExtra(Extra.KData, id);
            context.startService(intent);
            LogMgr.d(TAG, "启动绑定极光服务");
        }
    }

    //自定义消息
    @Override
    protected void onMessage(Context context, String message,String content, String title) {
        LogMgr.d(TAG, " 自定义消息 jpush onMessage = " + message);
        LogMgr.d(TAG, "接收到推送下来的自定义消息: content " + message);
        LogMgr.d(TAG, "接收到推送下来的自定义消息: content " + content);
        LogMgr.d(TAG, "接收到推送下来的自定义消息: content " + title);
        try {
            JPushMsg jPushMsg = new JPushMsg();
            jPushMsg.parse(message);
            LogMgr.d(TAG, "type = " + jPushMsg.getString(TJPushMsg.msgType) + "    " + "meetingId = " + jPushMsg.getString(TJPushMsg.meetId));

            Intent intent  = new Intent(context, MeetingDetailsActivity.class);
            intent.putExtra(Extra.KData, jPushMsg.getString(TJPushMsg.meetId));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);//点击后消失
            builder.setSmallIcon(R.mipmap.ic_launcher);//设置通知栏消息标题的头像
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
            builder.setContentText(content);//通知内容
            builder.setContentTitle(title);
            //利用PendingIntent来包装我们的intent对象,使其延迟跳转
            PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(intentPend);
            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

        } catch (JSONException e) {
            e.printStackTrace();
            LogMgr.d(TAG, " jpush msg 解析数据 error = " + e.getMessage());
        }
    }

    //普通消息
    @Override
    protected void onNotification(Context context, String message) {
        //解析数据
        LogMgr.d(TAG, " 普通消息 jpush onNotification = " + message);
    }

    //点击事件
    @Override
    protected void onOpenNotification(Context context) {

    }

}
