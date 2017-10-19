package yy.doctor.ui.frag.meeting.course;

import android.support.annotation.NonNull;

import inject.annotation.router.Arg;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;

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

    protected Course getCourse() {
        return mCourse;
    }

    protected String getMeetId() {
        return mMeetId;
    }

    @NonNull
    public Submit getSubmit() {
        if (mSubmit == null) {
            mSubmit = new Submit();
            mSubmit.put(TSubmit.detailId, getCourse().getLong(TCourse.id));
            mSubmit.put(TSubmit.finished, false);
        }
        return mSubmit;
    }

    @Override
    public void initData() {
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
