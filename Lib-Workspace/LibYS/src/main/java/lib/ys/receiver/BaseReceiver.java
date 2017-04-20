package lib.ys.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;

/**
 * 需要动态注册的广播接收器
 */
abstract public class BaseReceiver extends BroadcastReceiver {

    protected Context mContext;

    /**
     * 直接注册监听
     *
     * @param context
     */
    public BaseReceiver(Context context) {
        mContext = context;
    }

    abstract public void register();

    public void unRegister() {
        mContext.unregisterReceiver(this);
    }
}
