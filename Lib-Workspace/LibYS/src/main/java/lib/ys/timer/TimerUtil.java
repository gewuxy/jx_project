package lib.ys.timeTick;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {

    private final int KTimerInterval = 1000 / 60;

    private Timer mTimer;
    private Handler mHandler = null;
    private long mDelay;
    private TimerListener mCallback;

    public TimerUtil(TimerListener callback) {
        mCallback = callback;

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mCallback.onTimerTick();
            }
        };
    }

    public void setAttrs(long delay) {
        mDelay = delay;
    }

    public void start() {
        removeTimer();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, mDelay, KTimerInterval);
    }

    public void stop() {
        removeTimer();
    }

    private void removeTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
