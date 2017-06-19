package yy.doctor.frag.meeting.course;

import android.os.Bundle;
import android.support.annotation.Nullable;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseFrag;
import yy.doctor.Extra;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.TCourse;

/**
 * @auther : GuoXuan
 * @since : 2017/6/5
 */
public abstract class BaseCourseFrag extends BaseFrag {

    private Course mCourse;
    private String mMeetId;
    private long mStayTime;
    private boolean mStudy;

    private OnCourseListener mListener;

    @Override
    public void initData() {
        mStudy = false;
        Bundle b = getArguments();
        if (b != null) {
            mCourse = (Course) b.getSerializable(Extra.KData);
            mMeetId = b.getString(Extra.KMeetId);
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    protected Course getDetail() {
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

    protected void onPrepare(long allMilliseconds) {
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

    @Override
    protected void onVisible() {
        if (getVisible()) {
            mStudy =true;
            mStayTime = System.currentTimeMillis();
            YSLog.d(TAG,"onStart:-------"+ mStayTime);
        }
    }

    @Override
    protected void onInvisible() {
        mStayTime = System.currentTimeMillis() - mStayTime;
        if (mStudy) {
            mStayTime += mCourse.getLong(TCourse.studyTime, 0);
            YSLog.d(TAG,"onPause:--------"+ mStayTime);
            mCourse.put(TCourse.studyTime, mStayTime);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        YSLog.d(TAG,"onStart:");
    }

    @Override
    public void onStop() {
        super.onStop();
        YSLog.d(TAG,"onStop:");
    }

    @Override
    public void onResume() {
        super.onResume();
        YSLog.d(TAG,"onResume:");
    }

    @Override
    public void onPause() {
        super.onPause();
        YSLog.d(TAG,"onPause:");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YSLog.d(TAG,"onCreate:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YSLog.d(TAG,"onDestroy:");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YSLog.d(TAG,"onDestroyView:");
    }
}
