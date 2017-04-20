package lib.ys.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


abstract public class BatteryReceiver extends BaseReceiver {

    public BatteryReceiver(Context context) {
        super(context);
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(this, filter);
    }
}
