package lib.ys.timeTick;

import android.support.annotation.IntDef;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class InterpolatorUtil {

    public static final int KMax = 1;

    @IntDef({
            InterpolatorType.decelerate,
            InterpolatorType.overshoot,
            InterpolatorType.linear,
            InterpolatorType.accelerate,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface InterpolatorType {
        int decelerate = 1;
        int overshoot = 2;
        int linear = 3;
        int accelerate = 4;
    }

    private long mMoveStartTime;
    private long mMoveTotalTime;
    private Interpolator mInterpolator = null;
    private long mDelayTime;

    @InterpolatorType
    private int mType;

    public InterpolatorUtil() {
    }

    public void setAttrs(long duration, @InterpolatorType int type, long delayTime) {
        if (mMoveTotalTime != duration) {
            mMoveTotalTime = duration;
        }

        if (mType != type) {
            mType = type;
            switch (mType) {
                case InterpolatorType.decelerate: {
                    mInterpolator = new DecelerateInterpolator();
                }
                break;
                case InterpolatorType.overshoot: {
                    mInterpolator = new OvershootInterpolator();
                }
                break;
                case InterpolatorType.linear: {
                    mInterpolator = new LinearInterpolator();
                }
                break;
                case InterpolatorType.accelerate: {
                    mInterpolator = new AccelerateInterpolator();
                }
                break;
                default:
                    break;
            }
        }

        if (mDelayTime != delayTime) {
            mDelayTime = delayTime;
        }
    }

    public float getInterpolation() {
        long time = System.currentTimeMillis();
        long timeTemp = time - mMoveStartTime;

        if (timeTemp < 0) {
            return 0;
        } else {
            if (timeTemp >= mMoveTotalTime) {
                return KMax;
            }
        }

        float timeFactor = (float) timeTemp / (float) mMoveTotalTime;
        return mInterpolator.getInterpolation(timeFactor);
    }

    public void start() {
        mMoveStartTime = System.currentTimeMillis() + mDelayTime;
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    public long getDelayedTime() {
        return mDelayTime;
    }

    public long getMoveTotalTime() {
        return mMoveTotalTime;
    }
}
