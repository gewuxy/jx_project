package jx.doctor.ui.activity.meeting.play;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.List;

import inject.annotation.router.Route;
import lib.ys.YSLog;
import jx.doctor.R;
import jx.doctor.adapter.meeting.PptBreviaryAdapter;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.ui.activity.meeting.play.contract.PptLiveContract;
import jx.doctor.ui.activity.meeting.play.presenter.PptLivePresenterImpl;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;

/**
 * ppt直播(无视频)
 *
 * @auther : GuoXuan
 * @since : 2017/10/27
 */
@Route
public class PptLiveActivity extends BasePptActivity<PptLiveContract.View, PptLiveContract.Presenter> {

    @Override
    protected PptLiveContract.View createV() {
        return new PptLiveViewImpl();
    }

    @Override
    protected PptLiveContract.Presenter createP(PptLiveContract.View view) {
        return new PptLivePresenterImpl(view);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        getP().playMedia(position);
        if (position == getFragPpt().getCount() - 1) {
            getFragPpt().newVisibility(false);
        }
    }

    private class PptLiveViewImpl extends BasePptViewImpl implements PptLiveContract.View {

        @Override
        public boolean getNavBarLandscape() {
            return true;
        }

        @Override
        public int getControlResId() {
            return R.drawable.live_select_control;
        }

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
                int cur = getFragPpt().getCurrPosition();
                getFragPpt().removeCourse(c);
                getFragPpt().addCourse(course);
                addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        getFragPpt().setCurrPosition(cur);
                        removeOnGlobalLayoutListener(this);
                    }

                });
            } else {
                YSLog.d(TAG, "addCourse : add");
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
                        // DiscreteScrollView可能会没刷新到图片
                        PptBreviaryAdapter adapter = (PptBreviaryAdapter) getRv().getAdapter();
                        adapter.notifyDataSetChanged();
                        adapter.invalidate(adapter.getCount() - 1);
                        removeOnGlobalLayoutListener(this);
                    }

                });
            }
        }

        @Override
        public void refresh(Course course) {
            // 播放音频
            getFragPpt().startPlay();
        }
    }
}
