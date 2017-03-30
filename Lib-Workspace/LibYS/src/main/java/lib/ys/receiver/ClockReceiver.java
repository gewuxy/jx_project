package lib.ys.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


abstract public class ClockReceiver extends BaseReceiver {

    public ClockReceiver(Context context) {
        super(context);
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        mContext.registerReceiver(this, filter);
    }
}
