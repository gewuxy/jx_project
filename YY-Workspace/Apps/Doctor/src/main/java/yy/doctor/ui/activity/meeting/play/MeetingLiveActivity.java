package yy.doctor.ui.activity.meeting.play;

import android.os.Bundle;
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
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import yy.doctor.R;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.ui.activity.meeting.play.contract.MeetingLiveContract;
import yy.doctor.ui.activity.meeting.play.presenter.MeetingLivePresenterImpl;
import yy.doctor.ui.frag.meeting.PPTLiveFrag;
import yy.doctor.ui.frag.meeting.PPTRebFrag;
import yy.doctor.util.LandscapeSwitch;

/**
 * ppt直播(有视频)
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class MeetingLiveActivity extends BaseMeetingPlayActivity {

    @IntDef({
            PlayType.ppt,
            PlayType.live,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayType {
        int ppt = 0; // ppt
        int live = 1; // 直播
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

    private MeetingLiveContract.Presenter mPresenter;
    private MeetingLiveContract.View mView;

    @PlayType
    private int mPlayType; // 播放ppt还是播放直播

    private boolean mPlay; // 是否在播放声音

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        mView = new MeetingLiveViewImpl();
        mPresenter = new MeetingLivePresenterImpl(mView);

        mPlay = false;
    }

    @Override
    public void findViews() {
        super.findViews();

        mViewLive = findView(R.id.meet_live_layout_live);
        mFragLive = findFragment(R.id.meet_live_frag_live);
        mViewPpt = findView(R.id.meet_live_layout_ppt);
        mFragPpt = findFragment(R.id.meet_live_frag_ppt);

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
            if (v.getId() == R.id.meet_live_layout_ppt) {
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
        mPresenter.getDataFromNet(mMeetId, mModuleId);

        mFragPpt.setFragClickListener(() -> playPpt());

        mFragPpt.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                if (position == mFragPpt.getCount() - 1) {
                    mFragPpt.newVisibility(false);
                }
                if (mPlayType == PlayType.ppt && mPlay) {
                    // 在播放ppt
                    mFragPpt.startPlay();
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
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            mPresenter.getDataFromNet(mMeetId, mModuleId);
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
            case R.id.meet_live_layout_live: {
                mFragPpt.landscapeVisibility(false);
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
            case R.id.meet_live_layout_ppt: {
                if (orientationLandscape()) {
                    mFragPpt.landscapeVisibility(true);
                }
            }
            break;
        }
    }

    @Override
    protected void toLeft() {
        mFragPpt.setCurrentItem(-1, getString(R.string.course_first));
    }

    @Override
    protected void toRight() {
        mFragPpt.setCurrentItem(1, getString(R.string.course_last));
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_live;
    }

    @Override
    protected void portrait() {
        mViewPpt.setLayoutParams(mParamPpt);
        mViewLive.setLayoutParams(mParamLive);

        mViewPpt.setX(mLocationPpt[0]);
        mViewPpt.setY(mLocationPpt[1]);
        mViewLive.setX(mLocationLive[0]);
        mViewLive.setY(mLocationLive[1]);
        mFragPpt.setDispatch(false);

        mLandscapeSwitch.setDispatch(false);
        showView(R.id.meet_live_layout_p);
    }

    @Override
    protected void landscape() {
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
        goneView(R.id.meet_live_layout_p);
        showLandscapeView();
        mFragPpt.mediaVisibility(false);
    }

    @Override
    protected void toggle() {
        if (mPlay) {
            // 播放中(全部停掉)
            mFragPpt.closeVolume();
            mFragLive.closeAudio();
        } else {
            // 没有播放中
            if (mPlayType == PlayType.live) {
                mFragLive.startAudio();
            } else {
                mFragPpt.startVolume();
            }
        }
        mPlay = !mPlay;
        setPlayState(mPlay);
    }

    @Override
    protected int getControlResId() {
        return R.drawable.meet_play_live_select_control;
    }

    @Override
    protected void showLandscapeView() {
        mPresenter.starCount();
        showView(getNavBar());
        if (mPlayType == PlayType.ppt) {
            mFragPpt.landscapeVisibility(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFragLive.startPullStream();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mFragLive.stopPullStream();
        mPresenter.mediaStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenter.onDestroy();
    }

    private class MeetingLiveViewImpl extends BaseViewImpl implements MeetingLiveContract.View {

        @Override
        public void initView(PPT ppt) {
            if (ppt == null) {
                setViewState(ViewState.error);
                return;
            }
            setViewState(ViewState.normal);

            setTextComment(ppt.getInt(TPPT.count));

            String roomId = ppt.getString(TPPT.courseId);
            mFragLive.loginRoom(roomId);
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
                    mFragPpt.setCurrentItem(size - 1);
                    removeOnGlobalLayoutListener(this);
                }
            });
        }

        @Override
        public void addCourse(Course course, int index) {
            if (course != null) {
                mFragPpt.addCourse(course);
                int count = getPptFrag().getCount();
                setTextAll(count);
                if (count != getPptFrag().getCurrentItem()) {
                    // 不在最新页提示新的一页完成
                    getPptFrag().setTextNew(String.valueOf(count));
                    if (mPlayType == PlayType.live) {
                        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                mFragPpt.setCurrentItem();
                                mPlayType = PlayType.live;
                                removeOnGlobalLayoutListener(this);
                            }

                        });
                    }

                } else {
                    getPptFrag().startPlay();
                }
            }
        }

        @Override
        public void finishCount() {
            MeetingLiveActivity.this.goneView(getNavBar());
            mFragPpt.landscapeVisibility(false);
        }

        @Override
        public PPTRebFrag getPptFrag() {
            return mFragPpt;
        }

        @Override
        public void setTextOnline(int onlineNum) {
            MeetingLiveActivity.this.setTextOnline(onlineNum);
        }
    }

}
