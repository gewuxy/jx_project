package yy.doctor.util;

import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * 考试专用倒计时
 *
 * @auther : GuoXuan
 * @since : 2017/6/23
 */
public class ExamCount implements OnCountDownListener {

    private static final String TAG = ExamCount.class.getSimpleName().toString();

    private static ExamCount mInst = null;
    private OnCountListener mOnCountListener;
    private CountDown mCountDown;
    private long mSurplusTime; // 剩余时间

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

    public void prepared(long second) {
        stop();
        mSurplusTime = second;
        mCountDown = new CountDown(second);
        mCountDown.setListener(this);
    }

    public void start() {
        mCountDown.start();
    }

    public void start(long second) {
        stop();
        prepared(second);
        start();
    }

    public void pause() {
        stop();
        prepared(mSurplusTime);
    }

    public void stop() {
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    public void remove() {
        stop();
        mOnCountListener = null;
    }

    public long getSurplusTime() {
        return mSurplusTime;
    }

    @Override
    public void onCountDown(long remainCount) {
        mSurplusTime--;
        if (mOnCountListener != null) {
            mOnCountListener.onCount(remainCount);
        }
    }

    @Override
    public void onCountDownErr() {
    }
}
