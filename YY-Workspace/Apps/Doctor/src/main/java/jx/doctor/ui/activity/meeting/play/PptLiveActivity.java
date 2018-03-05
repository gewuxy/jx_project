package jx.doctor.ui.activity.meeting.play;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Route;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.ui.activity.meeting.play.contract.PptLiveContract;
import jx.doctor.ui.activity.meeting.play.presenter.PptLivePresenterImpl;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.util.Util;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;

/**
 * ppt直播(无视频)
 *
 * @auther : GuoXuan
 * @since : 2017/10/27
 */
@Route
public class PptLiveActivity extends BasePptActivity<PptLiveContract.View, PptLiveContract.Presenter> {

    private TextView mTvOnline;
    private View mIvControlL;

    @NonNull
    @Override
    protected PptLiveContract.View createV() {
        return new PptLiveViewImpl();
    }

    @NonNull
    @Override
    protected PptLiveContract.Presenter createP(PptLiveContract.View view) {
        return new PptLivePresenterImpl(view);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_ppt_live;
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        ViewGroup view = bar.addViewRight(R.drawable.play_audio_selector, v -> mIvControl.performClick());
        mIvControlL = Util.getBarView(view, ImageView.class);
        goneView(mIvControlL);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvOnline = findView(R.id.ppt_live_tv_online);
    }

    @Override
    public void setViews() {
        super.setViews();

        mV.setTextOnline(0);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        mP.playMedia(position);
        if (position == mFragPpt.getCount() - 1) {
            mFragPpt.newVisibility(false);
        }
    }

    private class PptLiveViewImpl extends BasePptViewImpl implements PptLiveContract.View {

        @Override
        public void onPlayState(boolean state) {
            super.onPlayState(state);

            mIvControlL.setSelected(state);
        }

        @Override
        public void portrait() {
            super.portrait();

            goneView(mIvControlL);
        }

        @Override
        public void landscape() {
            super.landscape();

            showView(mIvControlL);
        }

        @Override
        public void onNetworkSuccess(PPT ppt) {
            super.onNetworkSuccess(ppt);

            mFragPpt.setToLastPosition();
        }

        @Override
        public void addCourse(Course course) {
            int position = mFragPpt.getCount() - 1;
            BaseCourseFrag f = mFragPpt.getItem(position);
            if (f == null) {
                return;
            }
            Course c = f.getCourse();
            if (c == null) {
                return;
            }
            boolean temp = c.getBoolean(TCourse.temp);
            if (temp) {
                YSLog.d(TAG, "addCourse : update");
                int cur = mFragPpt.getCurrPosition();
                mFragPpt.removeCourse(c);
                mFragPpt.addCourse(course);
                addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        mFragPpt.setCurrPosition(cur);
                        removeOnGlobalLayoutListener(this);
                    }

                });
            } else {
                YSLog.d(TAG, "addCourse : add");
                mFragPpt.addCourse(course);
                addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        int count = mFragPpt.getCount();
                        if (count != mFragPpt.getCurrPosition()) {
                            // 不在最新页提示新的一页完成
                            mFragPpt.setTextNew(String.valueOf(count));
                        }
                        mTvAll.setText(fitNumber(count));
                        removeOnGlobalLayoutListener(this);
                    }

                });
            }
        }

        @Override
        public void setTextOnline(int onlineNum) {
            if (onlineNum < 0) {
                onlineNum = 0;
            }
            mTvOnline.setText(String.format(getString(R.string.online_num), onlineNum));
        }

        @Override
        public void refresh(Course course) {
            // 播放音频
            mFragPpt.startPlay();
        }
    }
}
