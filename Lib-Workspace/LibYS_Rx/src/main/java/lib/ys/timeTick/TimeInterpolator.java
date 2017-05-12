package lib.ys.timeTick;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 根据时间计算的Interpolator
 */
public class TimeInterpolator {

    public static final int KMax = 1;

    @IntDef({
            InterpolatorType.decelerate,
            InterpolatorType.overshoot,
            InterpolatorType.linear,
            InterpolatorType.accelerate,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface InterpolatorType {
        /**
         * 减速
         */
        int decelerate = 1;
        /**
         * 回弹
         */
        int overshoot = 2;
        /**
         * 匀速
         */
        int linear = 3;
        /**
         * 加速
         */
        int accelerate = 4;
    }

    private long mStartTime;
    private long mTotalTime;
    private Interpolator mInterpolator = null;
    private long mDelay;


    private TimeInterpolator() {
    }

    public static TimeInterpolator create(@InterpolatorType int type) {
        TimeInterpolator interpolator = new TimeInterpolator();

        switch (type) {
            case InterpolatorType.decelerate: {
                interpolator.mInterpolator = new DecelerateInterpolator();
            }
            break;
            case InterpolatorType.overshoot: {
                interpolator.mInterpolator = new OvershootInterpolator();
            }
            break;
            case InterpolatorType.linear: {
                interpolator.mInterpolator = new LinearInterpolator();
            }
            break;
            case InterpolatorType.accelerate: {
                interpolator.mInterpolator = new AccelerateInterpolator();
            }
            break;
        }

        return interpolator;
    }

    public void start(@IntRange(from = 0) long duration) {
        start(duration, mDelay);
    }

    public void start(@IntRange(from = 0) long duration, @IntRange(from = 0) long delay) {
        if (mTotalTime != duration) {
            mTotalTime = duration;
        }

        if (mDelay != delay) {
            mDelay = delay;
        }

        mStartTime = System.currentTimeMillis() + mDelay;
    }

    public float getInterpolation() {
        long time = System.currentTimeMillis();
        long timeTemp = time - mStartTime;

        if (timeTemp < 0) {
            return 0;
        } else {
            if (timeTemp >= mTotalTime) {
                return KMax;
            }
        }

        float timeFactor = (float) timeTemp / (float) mTotalTime;
        return mInterpolator.getInterpolation(timeFactor);
    }

    public void restart() {
        mStartTime = System.currentTimeMillis() + mDelay;
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    public long getDelay() {
        return mDelay;
    }

    public long getTotalTime() {
        return mTotalTime;
    }
}
