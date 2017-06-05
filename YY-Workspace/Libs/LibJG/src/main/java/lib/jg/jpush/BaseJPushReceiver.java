package lib.jg.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.jpush.android.api.JPushInterface;
import lib.ys.LogMgr;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
abstract public class BaseJPushReceiver extends BroadcastReceiver {

    private final String TAG = BaseJPushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        String action = intent.getAction();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogMgr.d(TAG, "接收Registration Id : " + regId);
            onRegistrationId(context, regId);

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            LogMgr.d(TAG, "接收到推送下来的自定义消息: " + message);
            onMessage(context, message);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
            String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogMgr.d(TAG, "接收到推送下来的通知的ID: " + notificationId);
            LogMgr.d(TAG, "onReceive: msg = " + msg);
            onNotification(context, msg);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogMgr.d(TAG, "用户点击打开了通知");
            onOpenNotification(context);
        }
        /**
         * 以下的消息不做接收
         */
//        else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//            LogMgr.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
//            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
//
//        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//            LogMgr.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
//        } else {
//            LogMgr.d(TAG, "Unhandled intent - " + intent.getAction());
//        }
    }

    /**
     * 接收到设备注册id
     *
     * @param id
     */
    abstract protected void onRegistrationId(Context context, String id);

    /**
     * 接收到自定义消息
     *
     * @param message
     */
    abstract protected void onMessage(Context context, String message);

    /**
     * 接收到通知消息, 会自动使用默认样式弹出到通知栏, 不需要做处理
     *
     * @param message
     */
    abstract protected void onNotification(Context context, String message);

    /**
     * 打开通知
     */
    abstract protected void onOpenNotification(Context context);
}
