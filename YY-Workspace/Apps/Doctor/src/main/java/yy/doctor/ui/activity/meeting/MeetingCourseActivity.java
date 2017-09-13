package yy.doctor.ui.activity.meeting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVPActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.serv.CommonServRouter;
import yy.doctor.ui.frag.meeting.course.AudioCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag.OnFragClickListener;
import yy.doctor.ui.frag.meeting.course.PicAudioCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.PicCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.VideoCourseFrag;
import yy.doctor.ui.frag.meeting.course.VideoCourseFragRouter;
import yy.doctor.util.NetPlayer;
import yy.doctor.util.NetPlayer.OnPlayerListener;
import yy.doctor.util.Time;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * 观看会议界面
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
@Route
public class MeetingCourseActivity extends BaseVPActivity implements
        OnCountDownListener,
        OnSeekBarChangeListener,
        OnPageChangeListener,
        OnPlayerListener,
        OnFragClickListener {

    private final int KVpSize = 3; // Vp缓存的数量
    private final int KViewPagerHDp = 270; // 每张PPT的高度
    private final int KVanishTime = 3; // 横屏显示时间

    @Arg
    String mMeetId; // 会议ID

    @Arg
    String mModuleId; // 模块ID

    private boolean mAutoPlay; // 不同PPT之间自动播放
    private long mAllMilliseconds; // 当前播放音频的总时长 (毫秒)
    private CountDown mCountDown; // 倒计时
    private PPT mPPT; // PPT
    private List<Course> mCourses; // PPT的内容
    private Map<Integer, Submit> mSubmits; // 学习时间

    private ViewGroup.LayoutParams mParams; // PPT 的布局参数

    private ImageView mIvControlP; // 控制按钮(竖屏)
    private ImageView mIvControlL; // 控制按钮(横屏)
    private TextView mTvTimeP; // 音频/视频播放的时间(竖屏)
    private TextView mTvTimeL; // 音频/视频播放的时间(横屏)
    private CircleProgressView mLayoutProgressP; // 进度(竖屏)
    private SeekBar mLayoutProgressL; // 进度(横屏)

    private TextView mTvSelect; // 当前页
    private TextView mTvAll; // 总页数

    private View mLayoutBar; // 占位图(NavBar)
    private View mLayoutBarMid; // NavBar中间
    private View mLayoutBarRight; // NavBar右间的按钮
    private View mLayoutControl; // 进度+控制按钮
    private View mLayoutP; // 竖屏布局
    private View mLayoutL; // 横屏布局

    private long mStartTime; // 开始时间
    private int mLastPosition; // 上一次的position

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mAutoPlay = true;
        mLastPosition = 0;
        mSubmits = new HashMap<>();
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundColor(Color.TRANSPARENT);

        bar.addViewLeft(R.drawable.nav_bar_ic_back, v -> {
            if (getOrientation()) {
                // 横屏
                Util.changeOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 切换为竖屏
            } else {
                // 竖屏
                finish();
            }
        });

        mLayoutBarMid = inflate(R.layout.layout_meeting_nav_bar_course);
        mTvSelect = (TextView) mLayoutBarMid.findViewById(R.id.meeting_nav_bar_select);
        mTvAll = (TextView) mLayoutBarMid.findViewById(R.id.meeting_nav_bar_all);
        bar.addViewMid(mLayoutBarMid);

        mLayoutBarRight = bar.addViewRight(R.drawable.meeting_ppt_ic_record,
                v -> MeetingRecordActivityRouter.create(getCurrentItem(), mPPT).route(MeetingCourseActivity.this, 0));
    }

    /**
     * 屏幕方向是否为横屏
     *
     * @return
     */
    private boolean getOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutBar = findView(R.id.meeting_ppt_view);

        mLayoutP = findView(R.id.meeting_ppt_layout_p);
        mIvControlP = findView(R.id.meeting_ppt_iv_control_p);
        mTvTimeP = findView(R.id.meeting_ppt_tv_time_p);
        mLayoutProgressP = findView(R.id.meeting_ppt_layout_progress);

        mLayoutL = findView(R.id.meeting_ppt_layout_l);
        mLayoutControl = findView(R.id.meeting_ppt_layout_control);
        mIvControlL = findView(R.id.meeting_ppt_iv_control_l);
        mTvTimeL = findView(R.id.meeting_ppt_tv_time_l);
        mLayoutProgressL = findView(R.id.meeting_ppt_sb_progress);
    }

    @Override
    public void setViews() {
        super.setViews();

        mParams = getViewPager().getLayoutParams(); // viewPager的布局参数
        setOffscreenPageLimit(KVpSize);
        setOnPageChangeListener(this);

        mLayoutL.setOnTouchListener(new TouchCancelListener());
        mTvTimeL.setOnTouchListener(new TouchCancelListener());
        mIvControlL.setOnTouchListener(new TouchCancelListener());
        mLayoutProgressL.setOnSeekBarChangeListener(this);

        setOnClickListener(R.id.meeting_ppt_iv_left);
        setOnClickListener(R.id.meeting_ppt_iv_right);
        setOnClickListener(R.id.meeting_ppt_iv_control_p);
        setOnClickListener(R.id.meeting_ppt_iv_control_l);
        setOnClickListener(R.id.meeting_ppt_iv_first);
        setOnClickListener(R.id.meeting_ppt_iv_comment);
        setOnClickListener(R.id.meeting_ppt_layout_control);

        refresh(RefreshWay.embed);
        getDataFromNet();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing
    }

    @Override
    public void onPageSelected(int position) {
        Util.noNetwork();

        saveStudyTime();

        setStatus(position);
        mLastPosition = position;

        onProgress(0, 0);

        NetPlayer.inst().stop();
        if (mCourses.get(position).getType() == CourseType.video) {
            VideoCourseFrag item = (VideoCourseFrag) getItem(position);
            NetPlayer.inst().setVideo(item.getTextureView());
        } else {
            NetPlayer.inst().setAudio();
        }
        NetPlayer.inst().prepare(mMeetId, getItem(position).getUrl());

        NetworkImageView.clearMemoryCache(MeetingCourseActivity.this);
        mAutoPlay = true;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }

    /**
     * 记录上一页的学习时间
     */
    private void saveStudyTime() {
        long curTime = System.currentTimeMillis();
        Submit submit = getItem(mLastPosition).getSubmit();
        long studyTime = submit.getLong(TSubmit.usedtime, 0);
        // 加上原来记录
        curTime += studyTime - mStartTime;
        submit.put(TSubmit.usedtime, curTime);
        mSubmits.put(mLastPosition, submit);

        YSLog.d(TAG, "saveStudyTime:" + mLastPosition + "------" + curTime);
    }

    /**
     * 切换PPT是页面重置
     */
    private void setStatus(int position) {
        if (mCourses.get(position).haveMedia()) {
            // 有音频的时候
            showView(mLayoutControl);
            showView(mTvTimeP);
        } else {
            // 没有音频的时候
            hideView(mLayoutControl);
            hideView(mTvTimeP);
        }
        mTvSelect.setText(String.valueOf(position + 1));
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mCourses.get(getCurrentItem()).haveMedia()) {
            onProgress(getCurrMilliseconds(), progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mCourses.get(getCurrentItem()).haveMedia()) {
            NetPlayer.inst().pause();
        }

        countDown(false);
        landscapeVisibility(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mCourses.get(getCurrentItem()).haveMedia()) {
            NetPlayer.inst().seekTo((int) (getCurrMilliseconds()));
            NetPlayer.inst().play();
        }

        countDown(true);
    }

    private long getCurrMilliseconds() {
        return mLayoutProgressL.getProgress() * mAllMilliseconds / NetPlayer.KMaxProgress;
    }

    /**
     * 倒计时
     *
     * @param state true 开始, false 结束
     */
    private void countDown(boolean state) {
        if (mCountDown == null) {
            mCountDown = new CountDown();
            mCountDown.setListener(MeetingCourseActivity.this);
        }
        if (state && getOrientation()) {
            mCountDown.start(KVanishTime);
        } else {
            mCountDown.stop();
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            landscapeVisibility(false);
        }
    }

    @Override
    public void onCountDownErr() {
        // do nothing
    }

    /**
     * 横屏的控件显示隐藏
     *
     * @param visibility true 显示, false 隐藏
     */
    private void landscapeVisibility(boolean visibility) {
        if (visibility && getOrientation()) {
            showView(getNavBar());
            showView(mLayoutL);
        } else {
            goneView(getNavBar());
            goneView(mLayoutL);
        }
    }

    private void getDataFromNet() {
        exeNetworkReq(MeetAPI.toCourse(mMeetId, mModuleId).build());
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), PPT.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<PPT> r = (Result<PPT>) result;
        if (r.isSucceed()) {
            mPPT = r.getData();
            if (mPPT == null) {
                return;
            }

            CourseInfo courseInfo = mPPT.getEv(TPPT.course);
            if (courseInfo == null) {
                return;
            }

            mCourses = courseInfo.getList(TCourseInfo.details);
            if (mCourses == null || mCourses.size() == 0) {
                return;
            }

            // 初始显示
            mTvAll.setText(String.valueOf(mCourses.size()));
            setStatus(0);

            // 逐个添加Frag
            for (Course course : mCourses) {
                addPPTFrag(course);
            }

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mCourses.get(0).getType() == CourseType.video) {
                        VideoCourseFrag item = (VideoCourseFrag) getItem(0);
                        NetPlayer.inst().setVideo(item.getTextureView());
                    } else {
                        NetPlayer.inst().setAudio();
                    }
                    NetPlayer.inst().prepare(mMeetId, getItem(0).getUrl());
                    removeOnGlobalLayoutListener(this);
                }
            });

            setViewState(ViewState.normal);

            invalidate();

        } else {
            showToast(r.getMessage());
        }
    }

    /**
     * 根据类型添加的frag
     */
    private void addPPTFrag(Course course) {
        BaseCourseFrag frag = null;
        switch (course.getType()) {
            case CourseType.audio: {
                frag = AudioCourseFragRouter.create(course, mMeetId).route();
            }
            break;
            case CourseType.video: {
                frag = VideoCourseFragRouter.create(course, mMeetId).route();
            }
            break;
            case CourseType.pic: {
                frag = PicCourseFragRouter.create(course, mMeetId).route();
            }
            break;
            case CourseType.pic_audio: {
                frag = PicAudioCourseFragRouter.create(course, mMeetId).route();
            }
            break;
        }

        if (frag != null) {
            frag.setListener(this);
            add(frag);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_ppt_iv_left: {
                // 上一页
                int preItem = getCurrentItem() - 1;
                if (preItem >= 0) {
                    setCurrentItem(preItem);
                } else {
                    showToast("这是第一页喔");
                }
            }
            break;
            case R.id.meeting_ppt_iv_right: {
                // 下一页
                int currNext = getCurrentItem() + 1;
                if (currNext < mCourses.size()) {
                    setCurrentItem(currNext);
                } else {
                    showToast("已是最后一页");
                }
            }
            break;
            case R.id.meeting_ppt_iv_control_p: // 横竖屏控制按钮操作一样(不加break)
            case R.id.meeting_ppt_iv_control_l: {
                // 控制
                NetPlayer.inst().toggle();
            }
            break;
            case R.id.meeting_ppt_iv_first: {
                // 切换到第一页
                setCurrentItem(0);
            }
            break;
            case R.id.meeting_ppt_iv_comment: {
                // 评论
                MeetingCommentActivityRouter.create(mMeetId).route(this);
            }
            break;
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            getDataFromNet();
        }
        return true;
    }

    @Override
    protected BaseCourseFrag getItem(int position) {
        return (BaseCourseFrag) super.getItem(position);
    }

    @Override
    public void onDownProgress(int progress) {
        // 下载进度(预留)
    }

    @Override
    public void onPreparedSuccess(long allMilliseconds) {
        mAllMilliseconds = allMilliseconds;
        if (mAutoPlay) {
            NetPlayer.inst().play();
            mAutoPlay = false;
        }
    }

    @Override
    public void onPreparedError() {
        // 准备失败
    }

    @Override
    public void onProgress(long currMilliseconds, int progress) {
        float seconds = (float) currMilliseconds / TimeUnit.SECONDS.toMillis(1);
        BigDecimal scale = new BigDecimal(String.format("%.1f", seconds)).setScale(0, BigDecimal.ROUND_HALF_UP);
        String text = Time.secondFormat(scale.longValue(), DateUnit.minute);
        YSLog.d(TAG, "onProgress:" + text);
        mTvTimeL.setText(text);
        mTvTimeP.setText(text);
        mLayoutProgressL.setProgress(progress);
        mLayoutProgressP.setProgress(progress);
    }

    @Override
    public void onPlayState(boolean state) {
        mIvControlL.setSelected(state);
        mIvControlP.setSelected(state);
    }

    @Override
    public void onCompletion() {
        getItem(mLastPosition).getSubmit().put(TSubmit.finished, true);
        mIvControlL.setSelected(false);
        mIvControlP.setSelected(false);
    }

    @Override
    public void onClick() {
        if (getOrientation()) {
            countDown(true);
            landscapeVisibility(true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            mParams.height = fitDp(KViewPagerHDp);
            getViewPager().setLayoutParams(mParams);
            countDown(false);
            landscapeVisibility(false);

            showView(mLayoutP);
            showView(mLayoutBar);
            showView(mLayoutBarMid);
            showView(mLayoutBarRight);
            showView(getNavBar());
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            mParams.height = MATCH_PARENT;
            getViewPager().setLayoutParams(mParams);
            countDown(true);
            landscapeVisibility(true);

            goneView(mLayoutP);
            goneView(mLayoutBar);
            goneView(mLayoutBarMid);
            goneView(mLayoutBarRight);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveStudyTime();
        NetPlayer.inst().pause();
        NetPlayer.inst().removeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mStartTime = System.currentTimeMillis(); // 下一页的开始时间
        NetPlayer.inst().setListener(this);
    }

    @Override
    protected void onDestroy() {
        saveStudyTime();
        countDown(false);

        notify(NotifyType.study_end);

        // 保持调用顺序
        super.onDestroy();

        // 拼接需要的数据
        JSONArray ja = new JSONArray();
        long studyTime;
        for (Integer key : mSubmits.keySet()) {
            Submit submit = mSubmits.get(key);
            studyTime = submit.getLong(TSubmit.usedtime, 0) / TimeUnit.SECONDS.toMillis(1);
            submit.put(TSubmit.usedtime, studyTime == 0 ? 1 : studyTime); // 至少传1秒
            ja.put(submit.toJsonObject());
        }

        Submit submit = new Submit();
        submit.put(TSubmit.meetId, mPPT.getString(TPPT.meetId));
        submit.put(TSubmit.moduleId, mPPT.getString(TPPT.moduleId));
        submit.put(TSubmit.courseId, mPPT.getString(TPPT.courseId));
        submit.put(TSubmit.times, ja.toString());

        // 把需要的对象传给服务提交(失败再次提交)
        CommonServRouter.create()
                .type(ReqType.course)
                .submit(submit)
                .route(this);

        NetPlayer.inst().recycle();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.meeting_finish) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        int currId = data.getIntExtra(Extra.KId, 0);
        if (currId != mLastPosition) {
            setCurrentItem(currId);
        }
    }

    /**
     * 按下取消倒计时
     */
    private class TouchCancelListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE: {
                    countDown(false);
                    landscapeVisibility(true);
                }
                break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    countDown(true);
                }
                break;
            }
            return false;
        }

    }

}