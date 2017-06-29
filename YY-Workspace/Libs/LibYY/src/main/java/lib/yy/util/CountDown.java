package lib.yy.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * 倒计时
 *
 * @author : GuoXuan
 * @since : 2017/5/25
 */

public class CountDown {

    private DisposableSubscriber<Long> mSub;

    private long mCount;
    private TimeUnit mTimeUnit;
    private OnCountDownListener mListener;

    public CountDown() {
        this(0, TimeUnit.SECONDS);
    }

    public CountDown(long count) {
        // 默认时间单位为秒
        this(count, TimeUnit.SECONDS);
    }

    public CountDown(@NonNull TimeUnit timeUnit) {
        this(0, timeUnit);
    }

    public CountDown(long count, @NonNull TimeUnit timeUnit) {
        mCount = count;
        mTimeUnit = timeUnit;
    }

    private DisposableSubscriber<Long> createSub() {
        if (mSub == null) {
            mSub = new DisposableSubscriber<Long>() {

                @Override
                public void onNext(@NonNull Long aLong) {
                    if (mListener != null) {
                        mListener.onCountDown(aLong);
                    }
                }

                @Override
                public void onError(@NonNull Throwable throwable) {
                    if (mListener != null) {
                        mListener.onCountDownErr();
                    }
                }

                @Override
                public void onComplete() {
                }
            };
        }
        return mSub;
    }

    /**
     * 开始倒数
     *
     * @return
     */
    public void start() {
        start(0, null);
    }

    public void start(long count) {
        start(count, null);
    }

    public void start(@NonNull TimeUnit unit) {
        start(0, unit);
    }

    public void start(long count, @Nullable TimeUnit unit) {
        dispose();

        if (count != 0) {
            mCount = count;
        }

        if (unit != null) {
            mTimeUnit = unit;
        }

        Flowable.interval(0, 1, mTimeUnit)//0秒延迟
                .take(mCount + 1) // 倒数次数
                .map(aLong -> mCount - aLong) // 转换成倒数的时间
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
    }

    public void start(Consumer<Subscription> consumer) {
        dispose();

        Flowable.interval(0, 1, mTimeUnit)//0秒延迟
                .take(mCount + 1) // 倒数次数
                .map(aLong -> mCount - aLong) // 转换成倒数的时间
                .doOnSubscribe(consumer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
    }

    public void stop() {
        dispose();
    }

    /**
     * 释放
     */
    private void dispose() {
        if (mSub != null && !mSub.isDisposed()) {
            mSub.dispose();
            mSub = null;
        }
    }

    public interface OnCountDownListener {
        void onCountDown(long remainCount);

        void onCountDownErr();
    }

    public void setListener(OnCountDownListener l) {
        mListener = l;
    }
}
