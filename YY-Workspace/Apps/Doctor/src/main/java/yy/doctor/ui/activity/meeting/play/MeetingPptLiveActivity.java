package yy.doctor.ui.activity.meeting.play;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.List;

import inject.annotation.router.Route;
import lib.ys.YSLog;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingBreviaryAdapter;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptLiveContract;
import yy.doctor.ui.activity.meeting.play.presenter.MeetingPptLivePresenterImpl;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;

/**
 * ppt直播(无视频)
 *
 * @auther : GuoXuan
 * @since : 2017/10/27
 */
@Route
public class MeetingPptLiveActivity extends BasePptActivity<MeetingPptLiveContract.View, MeetingPptLiveContract.Presenter> {

    private MeetingPptLiveContract.View mView;
    private MeetingPptLiveContract.Presenter mPresenter;

    @Override
    protected MeetingPptLiveContract.View createView() {
        if (mView == null) {
            mView = new MeetingPptLiveViewImpl();
        }
        return mView;
    }

    @Override
    protected MeetingPptLiveContract.Presenter createPresenter(MeetingPptLiveContract.View view) {
        if (mPresenter == null) {
            mPresenter = new MeetingPptLivePresenterImpl(view);
        }
        return mPresenter;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        mPresenter.playMedia(position);
        if (position == getFragPpt().getCount() - 1) {
            getFragPpt().newVisibility(false);
        }
    }

    @Override
    protected boolean getNavBarLandscape() {
        return true;
    }

    @Override
    protected int getControlResId() {
        return R.drawable.meet_play_live_select_control;
    }

    private class MeetingPptLiveViewImpl extends BasePptViewImpl implements MeetingPptLiveContract.View {

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            super.portraitInit(ppt, courses);
            onPlayState(true); // 非静音

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getFragPpt().setToLastPosition();
                    removeOnGlobalLayoutListener(this);
                }
            });
        }

        @Override
        public void addCourse(Course course) {
            int position = getFragPpt().getCount() - 1;
            BaseCourseFrag f = getFragPpt().getItem(position);
            Course c = f.getCourse();
            boolean temp = c.getBoolean(TCourse.temp);
            if (temp) {
                YSLog.d(TAG, "addCourse : update");
                getFragPpt().removeCourse(c);
                getFragPpt().addCourse(course);
            } else {
                YSLog.d(TAG, "addCourse : add");
                getFragPpt().addCourse(course);
            }
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int count = getFragPpt().getCount();
                    if (count != getFragPpt().getCurrPosition()) {
                        // 不在最新页提示新的一页完成
                        getFragPpt().setTextNew(String.valueOf(count));
                    }
                    setTextAll(count);
                    // DiscreteScrollView可能会没刷新到图片
                    MeetingBreviaryAdapter adapter = (MeetingBreviaryAdapter) getRv().getAdapter();
                    adapter.notifyDataSetChanged();
                    adapter.invalidate(adapter.getCount() - 1);
                    removeOnGlobalLayoutListener(this);
                }

            });
        }

        @Override
        public void refresh(Course course) {
            // 播放音频
            getFragPpt().startPlay();
        }
    }
}
