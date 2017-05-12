package lib.ys.timeTick;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subscribers.DisposableSubscriber;


/**
 * 计时器
 */
public class TimeTicker {

    private final int KTimerInterval = 1000 / 60;

    private OnTimeTickListener mListener;

    private DisposableSubscriber<Long> mSub;

    public TimeTicker(@NonNull OnTimeTickListener l) {
        if (l == null) {
            throw new NullPointerException("OnTimerListener can not be null");
        }
        mListener = l;
    }

    public void start() {
        start(0);
    }

    public void start(long delay) {
        stop();

        Flowable.interval(delay, KTimerInterval, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
    }

    public void stop() {
        dispose();
    }

    private void dispose() {
        if (mSub != null && !mSub.isDisposed()) {
            mSub.dispose();
            mSub = null;
        }
    }

    private DisposableSubscriber<Long> createSub() {
        mSub = new DisposableSubscriber<Long>() {

            @Override
            public void onNext(Long l) {
                mListener.onTimeTick();
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        };

        return mSub;
    }
}
