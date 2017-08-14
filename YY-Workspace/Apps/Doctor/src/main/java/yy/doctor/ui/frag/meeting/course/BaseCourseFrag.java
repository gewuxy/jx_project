package yy.doctor.ui.frag.meeting.course;

import router.annotation.Arg;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;
import yy.doctor.model.meet.Course;

/**
 * @auther : GuoXuan
 * @since : 2017/6/5
 */
public abstract class BaseCourseFrag extends BaseFrag {

    @Arg
    Course mCourse;

    @Arg
    String mMeetId;

    private OnCourseListener mListener;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    protected Course getCourse() {
        return mCourse;
    }

    protected String getMeetId() {
        return mMeetId;
    }

    abstract public void toggle();

    public interface OnCourseListener {
        /**
         * 准备工作
         *
         * @param allMilliseconds 播放总进度(毫秒)
         */
        void onPrepare(long allMilliseconds);

        void onPlay(boolean enablePlay, long allMilliseconds);

        /**
         * 播放中
         *
         * @param currMilliseconds 当前播放进度(毫秒)
         */
        void onProgress(long currMilliseconds);

        void onStop();

        void onClick();

        void end();
    }

    public void setOnPPTListener(OnCourseListener l) {
        mListener = l;
    }

    protected void onPrepared(long allMilliseconds) {
        if (mListener != null) {
            mListener.onPrepare(allMilliseconds);
        }
    }

    protected void onPlay(boolean enablePlay, long allMilliseconds) {
        if (mListener != null) {
            mListener.onPlay(enablePlay, allMilliseconds);
        }
    }

    protected void onProgress(long currMilliseconds) {
        if (mListener != null) {
            mListener.onProgress(currMilliseconds);
        }
    }

    protected void onPlayStop() {
        if (mListener != null) {
            mListener.onStop();
        }
    }

    protected void onCourseClick() {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    protected void end() {
        if (mListener != null) {
            mListener.end();
        }
    }

    public boolean preparePlay() {
        return false;
    }

    public void setRemainTime(int remainTime) {
    }

    public void pause() {
    }

    public void start() {
    }

    public void stop() {
    }

    public void seekTo(int msec) {
    }

    public abstract boolean isFinish();
}
