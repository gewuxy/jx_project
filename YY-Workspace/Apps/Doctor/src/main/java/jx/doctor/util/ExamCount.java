package jx.doctor.util;

import lib.ys.YSLog;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;

/**
 * 考试专用倒计时
 *
 * @auther : GuoXuan
 * @since : 2017/6/23
 */
public class ExamCount implements OnCountDownListener {

    public static final String TAG = ExamCount.class.getSimpleName().toString();

    private static ExamCount mInst = null;
    private OnCountListener mOnCountListener;
    private CountDown mCountDown; // 倒计时
    private long mRemainTime; // 剩余时间

    public interface OnCountListener {
        void onCount(long remainCount);
    }

    public synchronized static ExamCount inst() {
        if (mInst == null) {
            mInst = new ExamCount();
        }
        return mInst;
    }

    public void setOnCountListener(OnCountListener onCountListener) {
        mOnCountListener = onCountListener;
    }

    public long getRemainTime() {
        return mRemainTime;
    }

    public void start(long count) {
        if (count == 0) {
            return;
        }
        mRemainTime = count;

        if (mCountDown == null) {
            mCountDown = new CountDown();
            mCountDown.setListener(this);
        }
        mCountDown.start(count);
    }

    /**
     * 释放资源
     */
    public void remove() {
        if (mCountDown != null) {
            mCountDown.stop();
            mCountDown = null;
        }
        mOnCountListener = null;
    }

    @Override
    public void onCountDown(long remainCount) {
        mRemainTime--;
        YSLog.d(TAG, "onCountDown:mRemainTime" + mRemainTime);
        if (mOnCountListener != null) {
            YSLog.d(TAG, "onCountDown:remainCount" + remainCount);
            mOnCountListener.onCount(remainCount);
        }
    }

    @Override
    public void onCountDownErr() {
        YSLog.d(TAG, "onCountDownErr:");
    }
}
