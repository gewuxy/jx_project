package lib.ys.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


abstract public class ScreenOnOffReceiver extends BaseReceiver {

    public ScreenOnOffReceiver(Context context) {
        super(context);
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(this, filter);
    }
}
