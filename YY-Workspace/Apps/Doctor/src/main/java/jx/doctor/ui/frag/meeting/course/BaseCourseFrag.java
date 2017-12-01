package jx.doctor.ui.frag.meeting.course;

import android.os.Bundle;
import android.support.annotation.NonNull;

import inject.annotation.router.Arg;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.frag.base.BaseFrag;
import jx.doctor.model.meet.Submit;
import jx.doctor.model.meet.Submit.TSubmit;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.TCourse;

/**
 * @auther : GuoXuan
 * @since : 2017/6/5
 */
abstract public class BaseCourseFrag extends BaseFrag {

    @Arg
    Course mCourse;

    @Arg
    String mMeetId;

    private Submit mSubmit;
    private OnFragClickListener mListener;

    public interface OnFragClickListener {
        void onClick();
    }

    public void setListener(OnFragClickListener listener) {
        mListener = listener;
    }

    public Course getCourse() {
        return mCourse;
    }

    public void setCourse(Course course) {
        mCourse = course;
    }

    protected String getMeetId() {
        return mMeetId;
    }

    @NonNull
    public Submit getSubmit() {
        if (mSubmit == null) {
            mSubmit = new Submit();
            if (getCourse() != null) {
                mSubmit.put(TSubmit.detailId, getCourse().getLong(TCourse.id));
                mSubmit.put(TSubmit.finished, false);
            }
        }
        return mSubmit;
    }

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    protected void clickFrag() {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    abstract public String getUrl();

}
