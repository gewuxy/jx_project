package jx.doctor.ui.activity.meeting.play.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import jx.doctor.Constants;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.ui.activity.meeting.OverviewActivityRouter;
import jx.doctor.ui.activity.meeting.play.contract.RebContact;
import jx.doctor.util.NetPlayer;
import jx.doctor.util.Time;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */
public class RebPresenterImpl extends BasePlayPresenterImpl<RebContact.View> implements RebContact.Presenter {

    private int mLastPosition = Constants.KInvalidValue;

    public RebPresenterImpl(RebContact.View view) {
        super(view);
    }

    @Override
    public void playMedia(int position) {
        super.playMedia(position);

        if (mLastPosition != Constants.KInvalidValue && mLastPosition != mPosition) {
            setProgress(mLastPosition, 0);
            mLastPosition = position;
        }
    }

    @Override
    public void toggle(int index) {
        super.toggle(index);

        if (mPlay) {
            playMedia(index);
        } else {
            stopMedia();
        }
    }

    @Override
    protected void onMediaPrepared() {
        int progress = getProgress(mPosition);
        if (progress == 100) {
            progress = 0;
        }
        NetPlayer.inst().seekTo((int) (progress * mMediaTime / NetPlayer.KMaxProgress));
    }

    @Override
    protected void playProgress(String time, int progress) {
        setProgress(mPosition, progress);
        getView().playProgress(time, progress);
    }

    @Override
    protected void completion() {
        setProgress(mPosition, NetPlayer.KMaxProgress);
        getView().completion();
        mPlay = false;
    }

    private void setProgress(int index, int progress) {
        List<Course> courses = getCourses();
        if (courses != null && mPosition != Constants.KInvalidValue) {
            Course course = courses.get(index);
            if (course != null) {
                course.put(Course.TCourse.progress, progress);
            }
        }
    }

    private int getProgress(int index) {
        List<Course> courses = getCourses();
        if (courses != null && mPosition != Constants.KInvalidValue) {
            Course course = courses.get(index);
            if (course != null) {
                return course.getInt(Course.TCourse.progress, 0);
            }
        }
        return 0;
    }

    @Override
    public void toOverview(Context context, String title, int code) {
        OverviewActivityRouter.create(title, new ArrayList<>(getCourses())).route(context, code);
    }

    @Override
    public void changeTime(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > NetPlayer.KMaxProgress) {
            progress = NetPlayer.KMaxProgress;
        }
        setProgress(mPosition, progress);
        String time = Time.getTime((NetPlayer.KMaxProgress - progress) * mMediaTime / NetPlayer.KMaxProgress);
        getView().setTime(time);
    }

}
