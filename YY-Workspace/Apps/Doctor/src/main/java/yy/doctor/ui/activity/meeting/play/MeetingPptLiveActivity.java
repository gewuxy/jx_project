package yy.doctor.ui.activity.meeting.play;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.List;

import inject.annotation.router.Route;
import yy.doctor.R;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptLiveContract;
import yy.doctor.ui.activity.meeting.play.presenter.MeetingPptLivePresenterImpl;

/**
 * ppt直播(无视频)
 *
 * @auther : GuoXuan
 * @since : 2017/10/27
 */
@Route
public class MeetingPptLiveActivity extends BasePptActivity<MeetingPptLiveContract.View,MeetingPptLiveContract.Presenter> {

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
        public void addCourse(Course course, int index) {
            if (course == null) {
                // 播放音频
                getFragPpt().startPlay();
            } else {
                // 添加新的界面
                getFragPpt().addCourse(course);
                addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        int count = getFragPpt().getCount();
                        if (count != getFragPpt().getCurrPosition()) {
                            // 不在最新页提示新的一页完成
                            getFragPpt().setTextNew(String.valueOf(count));
                        }
                        setTextAll(count);
                        removeOnGlobalLayoutListener(this);
                    }

                });
            }
        }
    }
}
