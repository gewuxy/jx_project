package jx.doctor.ui.activity.meeting.play;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;

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
import jx.doctor.ui.frag.meeting.PPTRebFrag;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.util.LandscapeSwitch;
import jx.doctor.util.NetPlayer;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;

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

    private View mViewLive;
    private View mViewPpt;

    private PPTLiveFrag mFragLive;
    private PPTRebFrag mFragPpt;

    @PlayType
    private int mPlayType; // 播放ppt还是播放直播

    private boolean mPlay; // 是否在播放声音

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_live;
    }

    @Override
    public void initData() {
        super.initData();

        mPlay = false;
    }

    @Override
    public void findViews() {
        super.findViews();

        mViewLive = findView(R.id.live_layout_live);
        mFragLive = findFragment(R.id.live_frag_live);
        mViewPpt = findView(R.id.live_layout_ppt);
        mFragPpt = findFragment(R.id.live_frag_ppt);

    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mViewLive);
        setOnClickListener(mViewPpt);

        mLandscapeSwitch = new LandscapeSwitch(mViewPpt, mViewLive);
        mViewLive.setOnTouchListener(mLandscapeSwitch);
        mViewPpt.setOnTouchListener(mLandscapeSwitch);
        mLandscapeSwitch.setListener(v -> {
            if (v.getId() == R.id.live_layout_ppt) {
                mFragPpt.setDispatch(true);
                mFragPpt.refreshCurrentItem();
            } else {
                mFragPpt.setDispatch(false);
                playPpt();
            }
        });

        mParamPpt = (LayoutParams) mViewPpt.getLayoutParams();
        mParamLive = (LayoutParams) mViewLive.getLayoutParams();

        mLocationPpt = LandscapeSwitch.getLocation(mViewPpt);
        mLocationLive = LandscapeSwitch.getLocation(mViewLive);

        refresh(RefreshWay.embed);
        getP().getDataFromNet(mMeetId, mModuleId);

        mFragPpt.setFragClickListener(() -> playPpt());

        mFragPpt.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                NetPlayer.inst().stop();
                if (position == mFragPpt.getCount() - 1) {
                    mFragPpt.newVisibility(false);
                }
                if (mPlayType == PlayType.ppt) {
                    mFragPpt.setTextMedia("录音中");
                    if (mPlay) {
                        // 在播放ppt
                        mFragPpt.startPlay();
                    }
                } else {
                    mFragPpt.mediaVisibility(false);
                }
                setTextCur(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }

        });
    }

    @Override
    protected LiveContract.View createV() {
        return new LiveViewImpl();
    }

    @Override
    protected LiveContract.Presenter createP(LiveContract.View view) {
        return new LivePresenterImpl(view);
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            getP().getDataFromNet(mMeetId, mModuleId);
        }
        return true;
    }

    /**
     * 播放ppt
     */
    private void playPpt() {
        if (mPlayType == PlayType.ppt || !mPlay) {
            // 播放ppt状态
            return;
        }
        mPlayType = PlayType.ppt;
        // 停掉直播
        mFragLive.closeAudio();
        // 播放ppt
        mFragPpt.startPlay();
    }

    /**
     * 播放直播
     */
    @Override
    protected void onClick(int id) {
        switch (id) {
            case R.id.live_layout_live: {
                mFragPpt.landscapeVisibility(false);
                mFragPpt.setToLastPosition();
                if (mPlayType == PlayType.live || !mPlay) {
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
            case R.id.live_layout_ppt: {
                if (orientationLandscape()) {
                    mFragPpt.landscapeVisibility(true);
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
        getP().mediaStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getP().onDestroy();
    }

    private class LiveViewImpl extends BaseViewImpl implements LiveContract.View {

        @Override
        public void toLeft() {
            mFragPpt.offsetPosition(-1, getString(R.string.course_first));
        }

        @Override
        public void toRight() {
            mFragPpt.offsetPosition(1, getString(R.string.course_last));
        }

        @Override
        public boolean getNavBarLandscape() {
            return true;
        }

        @Override
        public void portrait() {
            mViewPpt.setLayoutParams(mParamPpt);
            mViewLive.setLayoutParams(mParamLive);

            mViewPpt.setX(mLocationPpt[0]);
            mViewPpt.setY(mLocationPpt[1]);
            mViewLive.setX(mLocationLive[0]);
            mViewLive.setY(mLocationLive[1]);
            mFragPpt.setDispatch(false);

            mLandscapeSwitch.setDispatch(false);
            LiveActivity.this.showView(R.id.live_layout_p);
        }

        @Override
        public void landscape() {
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
            LiveActivity.this.goneView(R.id.live_layout_p);
            showLandscapeView();
            mFragPpt.mediaVisibility(false);
        }

        @Override
        public void toggle() {
            if (mPlay) {
                // 播放中(全部停掉)
                mFragPpt.closeVolume();
                mFragLive.closeAudio();
            } else {
                // 没有播放中
                mFragPpt.startVolume();
                mFragLive.startAudio();
            }
            mPlay = !mPlay;
            setPlayState(mPlay);
        }

        @Override
        public int getControlResId() {
            return R.drawable.live_select_control;
        }

        @Override
        public void showLandscapeView() {
            getP().starCount();
            showView(getNavBar());
            if (mPlayType == PlayType.ppt) {
                mFragPpt.landscapeVisibility(true);
            }
        }

        @Override
        public void initView(PPT ppt) {
            if (ppt == null) {
                setViewState(ViewState.error);
                return;
            }
            setViewState(ViewState.normal);

            setTextComment(ppt.getInt(TPPT.count));

            String pullUrl = ppt.getString(TPPT.pullUrl);
            mFragLive.setPlayUrl(pullUrl);
            mPlayType = PlayType.live;

            mPlay = true;
            setPlayState(mPlay);

            mFragPpt.setPPT(ppt);
            mFragPpt.addCourses();

            CourseInfo courseInfo = ppt.get(TPPT.course);

            if (courseInfo == null) {
                setViewState(ViewState.error);
                return;
            }

            setTextTitle(courseInfo.getString(TCourseInfo.title));
            List<Course> courses = courseInfo.getList(TCourseInfo.details);
            if (courses == null) {
                setViewState(ViewState.error);
                return;
            }
            int size = courses.size();
            setTextCur(size);
            setTextAll(size);
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
            Course c = f.getCourse();
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
                        int count = getPptFrag().getCount();
                        setTextAll(count);
                        if (count != getPptFrag().getCurrPosition()) {
                            // 不在最新页提示新的一页
                            if (mPlayType == PlayType.live) {
                                mFragPpt.setToLastPosition();
                            } else {
                                getPptFrag().setTextNew(String.valueOf(count));
                            }
                        }
                        removeOnGlobalLayoutListener(this);
                    }

                });
            }
        }

        @Override
        public void refresh(Course course) {
            // 推了音频过来
            if (mPlayType == PlayType.ppt) {
                getPptFrag().startPlay();
            }
        }

        @Override
        public void finishCount() {
            LiveActivity.this.goneView(getNavBar());
            mFragPpt.landscapeVisibility(false);
        }

        @Override
        public PPTRebFrag getPptFrag() {
            return mFragPpt;
        }

        @Override
        public void startPull() {
            mFragLive.startPullStream();
        }

        @Override
        public void setTextOnline(int onlineNum) {
            LiveActivity.this.setTextOnline(onlineNum);
        }

        @Override
        public void nextItem() {
            if (mPlayType == PlayType.ppt) {
                getPptFrag().offsetPosition(1, "");
            }
        }
    }

}
