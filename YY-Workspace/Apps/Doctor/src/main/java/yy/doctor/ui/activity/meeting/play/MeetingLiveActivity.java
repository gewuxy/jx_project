package yy.doctor.ui.activity.meeting.play;

import android.content.pm.ActivityInfo;
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
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.Live;
import yy.doctor.model.meet.ppt.Live.TLive;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.ui.frag.meeting.PPTLiveFrag;
import yy.doctor.ui.frag.meeting.PPTRebFrag;
import yy.doctor.util.LandscapeSwitch;

/**
 * 直播
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
    public void initData() {
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
                mFragPpt.setClickable(false);
            } else {
                mFragPpt.setClickable(true);
            }
        });

        mParamPpt = (LayoutParams) mViewPpt.getLayoutParams();
        mParamLive = (LayoutParams) mViewLive.getLayoutParams();

        mLocationPpt = LandscapeSwitch.getLocation(mViewPpt);
        mLocationLive = LandscapeSwitch.getLocation(mViewLive);

        refresh(RefreshWay.embed);
        mPresenter.getDataFromNet(mMeetId, mModuleId);

        mFragPpt.setFragClickListener(() -> {
            if (mPlayType == PlayType.ppt || !mPlay) {
                // 播放ppt状态
                return;
            }
            mPlayType = PlayType.ppt;
            // 停掉直播
            mFragLive.stopAudio();
            // 播放ppt
            mFragPpt.startPlay();
        });

        mFragPpt.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
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
    protected void onClick(int id) {
        switch (id) {
            case R.id.meet_live_layout_live: {
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

        mFragPpt.setClickable(true);

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
        if (mPlayType == PlayType.live) {
            mLandscapeSwitch.setViewB(mViewLive);
            mFragPpt.setClickable(true);
        } else {
            mLandscapeSwitch.setViewB(mViewPpt);
            mFragPpt.setClickable(false);
        }
        mLandscapeSwitch.setDispatch(true);
        goneView(R.id.meet_live_layout_p);
    }

    @Override
    protected void toggle() {
        if (mPlay) {
            // 播放中(全部停掉)
            mFragPpt.stopPlay();
            mFragLive.stopAudio();
        } else {
            // 没有播放中
            if (mPlayType == PlayType.live) {
                mFragLive.startAudio();
            } else {
                mFragPpt.startPlay();
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
    public void onBackPressed() {
        // fixme: 测试方便(需求不需要)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        goneView(getNavBar());
        portrait();
    }

    private class MeetingLiveViewImpl implements MeetingLiveContract.View {

        @Override
        public void showToast(String content) {
            MeetingLiveActivity.this.showToast(content);
        }

        @Override
        public void initView(PPT ppt) {
            if (ppt == null) {
                return;
            }
            setViewState(ViewState.normal);

            String roomId = ppt.getString(TPPT.courseId);
            mFragLive.loginRoom(roomId);
            mPlayType = PlayType.live;

            mPlay = true;
            setPlayState(mPlay);

            mFragPpt.setPPT(ppt);

            Live live = ppt.get(TPPT.videoLive);
            if (live == null) {
                return;
            }
            int page = live.getInt(TLive.livePage, 1);
            for (int i = 0; i < page; i++) {
                mFragPpt.addCourse(i);
            }
            setTextCur(page);
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mFragPpt.setCurrentItem(page - 1);
                    removeOnGlobalLayoutListener(this);
                }
            });

            CourseInfo courseInfo = ppt.get(TPPT.course);
            if (courseInfo == null) {
                return;
            }
            List courses = courseInfo.getList(TCourseInfo.details);
            if (courses == null) {
                return;
            }
            setTextAll(courses.size());
        }

        @Override
        public void addCourse(int position) {
            if (position < mFragPpt.getCount()) {
                return;
            }
            mFragPpt.addCourse(position);
            if (mPlayType == PlayType.live) {
                addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        mFragPpt.setCurrentItem(position);
                        removeOnGlobalLayoutListener(this);
                    }

                });
            } else {
                // 提示新的一页完成
            }
        }
    }

}
