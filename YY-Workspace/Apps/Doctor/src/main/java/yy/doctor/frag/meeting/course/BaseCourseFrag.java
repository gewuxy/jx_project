package yy.doctor.frag.meeting.course;

import android.os.Bundle;

import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseFrag;
import yy.doctor.Extra;
import yy.doctor.model.meet.Course;

/**
 * @auther : GuoXuan
 * @since : 2017/6/5
 */

public abstract class BaseCourseFrag extends BaseFrag {

    private Course mCourse;
    private String mMeetId;

    private OnPPTListener mListener;


    @Override
    public void initData() {
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

    public interface OnPPTListener {
        /**
         * 准备工作
         *
         * @param enablePlay 是否有播放功能(音视频)
         */
        void onPrepare(boolean enablePlay);

        void onStart(boolean enablePlay);

        /**
         * 播放中
         *
         * @param currSeconds 当前播放进度(秒)
         */
        void onProgress(long currSeconds);

        void onStop();
    }

    public void setOnPPTListener(OnPPTListener l) {
        mListener = l;
    }

    protected void onPrepare(boolean enablePlay) {
        if (mListener != null) {
            mListener.onPrepare(enablePlay);
        }
    }

    protected void onStart(boolean enablePlay) {
        if (mListener != null) {
            mListener.onStart(enablePlay);
        }
    }

    protected void onProgress(long currSeconds) {
        if (mListener != null) {
            mListener.onProgress(currSeconds);
        }
    }

    protected void onPlayStop() {
        if (mListener != null) {
            mListener.onStop();
        }
    }
}
