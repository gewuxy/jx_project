package jx.doctor.ui.activity.meeting.play;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import inject.annotation.router.Route;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.model.meet.ppt.CourseInfo;
import jx.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.model.meet.ppt.PPT.TPPT;
import jx.doctor.ui.activity.meeting.play.contract.LiveContract;
import jx.doctor.ui.activity.meeting.play.presenter.LivePresenterImpl;
import jx.doctor.ui.frag.meeting.PPTLiveFrag;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.util.LandscapeSwitch;
import jx.doctor.util.Util;
import lib.ys.YSLog;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;

/**
 * ppt直播(有视频)
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class LiveActivity extends BasePlayActivity<LiveContract.View, LiveContract.Presenter> {

    @IntDef({
            PlayType.ppt,
            PlayType.live,
            PlayType.reset,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayType {
        int ppt = 0; // ppt
        int live = 1; // 直播
        int reset = 2;
    }

    private LayoutParams mParamPpt;
    private LayoutParams mParamLive;

    private int[] mLocationPpt; // Ppt竖屏位置信息
    private int[] mLocationLive; // 直播竖屏位置信息

    private LandscapeSwitch mLandscapeSwitch; // 横屏管理
    private PPTLiveFrag mFragLive;

    private View mViewLive;
    private View mIvControlL;
    private TextView mTvTitle;
    private TextView mTvOnline;
    private ImageView mIvTitle;

    @PlayType
    private int mPlayType; // 播放ppt还是播放直播

    @Override
    public int getContentViewId() {
        return R.layout.activity_live;
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        mTvOnline = bar.addTextViewRight(String.format(getString(R.string.online_num), 0), null);
        ViewGroup view = bar.addViewRight(R.drawable.play_audio_selector, v -> mIvControl.performClick());
        mIvControlL = Util.getBarView(view, ImageView.class);
        goneView(mIvControlL);
    }

    @Override
    public void findViews() {
        super.findViews();

        mViewLive = findView(R.id.live_layout_live);
        mFragLive = findFragment(R.id.live_frag_live);
        mFragPpt = findFragment(R.id.play_frag_ppt);

        mTvTitle = findView(R.id.live_tv_title);
        mIvTitle = findView(R.id.live_iv_title);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mViewLive);
        setOnClickListener(mLayoutFrag);
        setOnClickListener(R.id.live_layout_title);

        mLandscapeSwitch = new LandscapeSwitch(mLayoutFrag, mViewLive);
        mViewLive.setOnTouchListener(mLandscapeSwitch);
        mLayoutFrag.setOnTouchListener(mLandscapeSwitch);
        mLandscapeSwitch.setListener(v -> {
            if (v.getId() == R.id.play_layout_frag_ppt) {
                mFragPpt.setDispatch(true);
                mFragPpt.refreshCurrentItem();
            } else {
                mFragPpt.setDispatch(false);
                playPpt();
            }
        });

        mParamPpt = (LayoutParams) mLayoutFrag.getLayoutParams();
        mParamLive = (LayoutParams) mViewLive.getLayoutParams();

        mLocationPpt = LandscapeSwitch.getLocation(mLayoutFrag);
        mLocationLive = LandscapeSwitch.getLocation(mViewLive);

        mFragPpt.setFragClickListener(this::playPpt);

        mTvTitle.setText(mTitle);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (position == mFragPpt.getCount() - 1) {
            mFragPpt.newVisibility(false);
        }
        if (mPlayType == PlayType.ppt) {
            if (mIvControl.isSelected()) {
                // 在播放ppt
                mFragPpt.startPlay();
            }
        } else {
        }
    }

    @NonNull
    @Override
    protected LiveContract.View createV() {
        return new LiveViewImpl1();
    }

    @NonNull
    @Override
    protected LiveContract.Presenter createP(LiveContract.View view) {
        return new LivePresenterImpl(view);
    }

    /**
     * 播放ppt
     */
    private void playPpt() {
        if (mFragPpt.landscapeVisibility() == View.VISIBLE) {
            mFragPpt.landscapeVisibility(false);
        } else {
            countStart();
        }
        if (mPlayType == PlayType.ppt || !mIvControl.isSelected()) {
            // 播放ppt状态
            return;
        }
        mPlayType = PlayType.ppt;
        // 停掉直播
        mFragLive.closeAudio();
        // 播放ppt
        mFragPpt.startPlay();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.play_nav_iv_control: {
                if (mIvControl.isSelected()) {
                    // 打开声音
                    mFragPpt.startVolume();
                    mFragLive.startAudio();
                } else {
                    // 关闭声音
                    mFragPpt.closeVolume();
                    mFragLive.closeAudio();
                }
            }
            break;
            case R.id.live_layout_live: {
                mFragPpt.landscapeVisibility(false);
                mFragPpt.setToLastPosition();
                if (orientation()) {
                    if (getNavBar().getVisibility() == View.VISIBLE) {
                        showView(getNavBar());
                        mP.startCount();
                    } else {
                        goneView(getNavBar());
                    }
                }
                if (mPlayType == PlayType.live || !mIvControl.isSelected()) {
                    // 直播状态
                    return;
                }
                mPlayType = PlayType.live;
                // 停掉PPT
                mFragPpt.stopPlay();
                // 播放直播
                mFragLive.startAudio();
            }
            break;
            case R.id.live_layout_title: {
                if (mIvTitle.getVisibility() == View.VISIBLE) {
                    goneView(mIvTitle);
                    showView(mTvTitle);
                } else {
                    showView(mIvTitle);
                    goneView(mTvTitle);
                }
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFragLive.startPullStream();
        mPlayType = PlayType.reset;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mFragLive.stopPullStream();
    }

    private class LiveViewImpl1 extends BasePlayViewImpl implements LiveContract.View {

        @Override
        public void portrait() {
            super.portrait();

            mLayoutFrag.setLayoutParams(mParamPpt);
            mViewLive.setLayoutParams(mParamLive);

            mLayoutFrag.setX(mLocationPpt[0]);
            mLayoutFrag.setY(mLocationPpt[1]);
            mViewLive.setX(mLocationLive[0]);
            mViewLive.setY(mLocationLive[1]);

            mFragPpt.setDispatch(false);
            mLandscapeSwitch.setDispatch(false);

            showView(mTvOnline);
            goneView(mIvControlL);
        }

        @Override
        public void landscape() {
            super.landscape();

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mLandscapeSwitch.initLandscape();
                    removeOnGlobalLayoutListener(this);
                }

            });
            // 默认要放直播
            mLandscapeSwitch.setViewB(mViewLive);
            mFragPpt.setDispatch(true);
            mViewLive.performClick();

            mFragPpt.refreshCurrentItem();

            mLandscapeSwitch.setDispatch(true);

            goneView(mTvOnline);
            showView(mIvControlL);
        }

        @Override
        public void onNetworkSuccess(PPT ppt) {
            super.onNetworkSuccess(ppt);

            if (ppt == null) {
                return;
            }

            String pullUrl = ppt.getString(TPPT.pullUrl);
            mFragLive.setPlayUrl(pullUrl);
            mPlayType = PlayType.live;

            CourseInfo courseInfo = ppt.get(TPPT.course);
            if (courseInfo == null) {
                setViewState(ViewState.error);
                return;
            }

            List<Course> courses = courseInfo.getList(TCourseInfo.details);
            if (courses == null) {
                setViewState(ViewState.error);
                return;
            }
            int size = courses.size();
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mFragPpt.setCurrPosition(size - 1);
                    removeOnGlobalLayoutListener(this);
                }
            });
        }

        @Override
        public void addCourse(Course course) {
            // 同步 fixme:待整理
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
                            // 不在最新页提示新的一页
                            if (mPlayType == PlayType.live) {
                                mFragPpt.setToLastPosition();
                            } else {
                                mFragPpt.setTextNew(String.valueOf(count));
                            }
                        }
                        mTvAll.setText(fitNumber(count));
                        removeOnGlobalLayoutListener(this);
                    }

                });
            }
        }

        @Override
        public void refresh(Course course) {
            // 推了音频过来
            if (mPlayType == PlayType.ppt) {
                mFragPpt.startPlay();
            }
        }

        @Override
        public void startPull() {
            mFragLive.startPullStream();
        }

        @Override
        public void onPlayState(boolean state) {
            super.onPlayState(state);

            mIvControlL.setSelected(state);
        }

        @Override
        public void setTextOnline(int onlineNum) {
            if (onlineNum < 0) {
                onlineNum = 0;
            }
            mTvOnline.setText(String.format(getString(R.string.online_num), onlineNum));
        }

        @Override
        public void nextItem() {
            if (mPlayType == PlayType.ppt) {
                mFragPpt.offsetPosition(1, "");
            }
        }

    }

}
