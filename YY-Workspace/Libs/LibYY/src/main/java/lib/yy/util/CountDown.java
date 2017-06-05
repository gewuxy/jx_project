package lib.yy.util;

import android.support.annotation.NonNull;

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

    public CountDown(long secondTime) {
        this(secondTime, TimeUnit.SECONDS);
    }

    public CountDown(long time, @NonNull TimeUnit timeUnit) {
        mCount = time;
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
//                    CountDown.this.onError(throwable);
                }

                @Override
                public void onComplete() {
//                    CountDown.this.onComplete();
                }
            };
        }
        return mSub;
    }

    /**
     * 切换到主线程开始倒数
     *
     * @return
     */
//    public CountDown startOnMain() {
//        recycle();
//        mFlowable.observeOn(AndroidSchedulers.mainThread())
//                .subscribe(createSub());
//        return this;
//    }
    public void start() {
        start(0);
    }

    public void start(long count) {
        dispose();

        if (count != 0) {
            mCount = count;
        }

        Flowable.interval(0, 1, mTimeUnit)//0秒延迟
                .take(mCount + 1) // 倒数次数
                .map(aLong -> mCount - aLong) // 转换成倒数的时间
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
    }

    /**
     * 在onNext之前设置
     * 并切换到主线程开始倒数
     *
     * @param consumer
     */
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
        void onCountDownErr();
        void onCountDown(long remainCount);
    }

    public void setListener(OnCountDownListener l) {
        mListener = l;
    }
}
