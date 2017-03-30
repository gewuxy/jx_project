package lib.ys.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


abstract public class TimeTickReceiver extends BaseReceiver {

    public TimeTickReceiver(Context context) {
        super(context);
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        mContext.registerReceiver(this, filter);
    }
}
