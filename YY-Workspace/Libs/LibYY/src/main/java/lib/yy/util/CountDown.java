package lib.yy.util;

import android.support.annotation.NonNull;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
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

    private Flowable<Long> mFlowable;
    private DisposableSubscriber<Long> mSub;

    public CountDown(long secondTime) {
        this(secondTime, TimeUnit.SECONDS);
    }

    public CountDown(long time, @NonNull TimeUnit timeUnit) {
        mFlowable = Flowable.interval(0, 1, timeUnit)//0秒延迟
                .take(time + 1) // 倒数次数
                .map(aLong -> time - aLong);// 转换成倒数的时间
    }

    private DisposableSubscriber<Long> createSub() {
        if (mSub == null) {
            mSub = new DisposableSubscriber<Long>() {

                @Override
                public void onNext(@NonNull Long aLong) {
                    CountDown.this.onNext(aLong);
                }

                @Override
                public void onError(@NonNull Throwable throwable) {
                    CountDown.this.onError(throwable);
                }

                @Override
                public void onComplete() {
                    CountDown.this.onComplete();
                }
            };
        }
        return mSub;
    }

    public Flowable<Long> getFlowable() {
        return mFlowable;
    }

    /**
     * 切换到主线程开始倒数
     * @return
     */
    public CountDown startOnMain() {
        recycle();
        mFlowable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
        return this;
    }

    /**
     * 在onNext之前设置
     * 并切换到主线程开始倒数
     *
     * @param consumer
     */
    private CountDown startOnMain(Consumer<Subscription> consumer) {
        recycle();
        mFlowable.doOnSubscribe(consumer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
        return this;
    }

    /**
     * 每倒数一次
     *
     * @param aLong 倒数的时间
     */
    public void onNext(Long aLong) {
    }

    /**
     * 倒数出错
     *
     * @param throwable
     */
    public void onError(Throwable throwable) {
    }

    /**
     * 倒数结束
     */
    public void onComplete() {
    }

    /**
     * 释放
     */
    public void recycle() {
        if (mSub != null && !mSub.isDisposed()) {
            mSub.dispose();
            mSub = null;
        }
    }
}
