package yy.doctor.receiver;

import android.content.Context;

import lib.jg.jpush.BaseJPushReceiver;
import lib.ys.LogMgr;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class JPushReceiver extends BaseJPushReceiver {

    @Override
    protected void onRegistrationId(Context context, String id) {
        LogMgr.d("www", "onRegistrationId: id = " + id);
    }

    @Override
    protected void onMessage(Context context, String message) {

    }

    @Override
    protected void onNotification(Context context, String message) {

    }

    @Override
    protected void onOpenNotification(Context context) {

    }

}
