package yy.doctor.ui.activity.meeting.play;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.List;

import inject.annotation.router.Route;
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
public class MeetingPptLiveActivity extends BaseMeetingPptActivity<MeetingPptLiveContract.View, MeetingPptLiveContract.Presenter> {

    @Override
    protected void set() {
    }

    @Override
    protected MeetingPptLiveViewImpl createView() {
        return new MeetingPptLiveViewImpl();
    }

    @Override
    protected MeetingPptLiveContract.Presenter createPresenter(MeetingPptLiveContract.View view) {
        return new MeetingPptLivePresenterImpl(view);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (position == getFragPpt().getCount() - 1) {
            getFragPpt().newVisibility(false);
        }
    }

    private class MeetingPptLiveViewImpl extends MeetingPptViewImpl implements MeetingPptLiveContract.View {

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            super.portraitInit(ppt, courses);

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getFragPpt().setCurrentItem();
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
                int count = getFragPpt().getCount();
                if (count == getFragPpt().getCurrentItem()) {
                    // 不在最新页提示新的一页完成
                    getFragPpt().setTextNew(String.valueOf(count));
                }
                setTextAll(count);
            }
        }
    }
}
