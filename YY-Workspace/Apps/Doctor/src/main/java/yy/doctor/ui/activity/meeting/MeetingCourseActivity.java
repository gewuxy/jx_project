package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
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
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.MeetParam;
import yy.doctor.serv.CommonServ;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.ui.frag.meeting.course.AudioCourseFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag.OnCourseListener;
import yy.doctor.ui.frag.meeting.course.PicAudioCourseFrag;
import yy.doctor.ui.frag.meeting.course.PicCourseFrag;
import yy.doctor.ui.frag.meeting.course.VideoCourseFrag;
import yy.doctor.util.Time;
import yy.doctor.view.CircleProgressView;

/**
 * 观看会议界面
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
public class MeetingCourseActivity extends BaseVPActivity implements OnCountDownListener {

    private static final int KVpSize = 3; // Vp缓存的数量
    private final int KViewPagerHDp = 270; // 每张PPT的高度
    private final int KVanishTime = 3; // 横屏显示时间

    private String mMeetId; // 会议ID
    private String mModuleId; // 模块ID
    private boolean mIsPortrait; // 是否为竖屏
    private long mAllMilliseconds; // 当前播放音频的总时长 (毫秒)
    private CountDown mCountDown; // 倒计时
    private OnCourseListener mListener;
    private PPT mPPT; // PPT
    private List<Course> mCourses; // PPT的内容
    private Map<Integer, Long> mTimes; // 学习时间

    private ViewGroup.LayoutParams mParams; // PPT 的布局参数

    private ImageView mIvControlP; // 控制按钮(竖屏)
    private ImageView mIvControlL; // 控制按钮(横屏)
    private CircleProgressView mLayoutCp; // 进度(竖屏)
    private SeekBar mSb; // 进度(横屏)

    private TextView mTvSelect; // 当前页
    private TextView mTvAll; // 总页数
    private TextView mTvTimeP; // 音频/视频播放的时间(竖屏)
    private TextView mTvTimeL; // 音频/视频播放的时间(横屏)

    private View mLayoutBar; // 占位图(NavBar)
    private View mLayoutBarMid; // NavBar中间
    private View mLayoutBarRight; // NavBar右间的按钮
    private View mLayoutControl; // 进度+控制按钮
    private View mLayoutP; // 竖屏布局
    private View mLayoutL; // 横屏布局

    private long mStartTime; // 开始时间
    private int mLastPosition; // 上一次的position


    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, MeetingCourseActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);

        mStartTime = System.currentTimeMillis();
        mLastPosition = 0;
        mTimes = new HashMap<>();

        mListener = new OnCourseListener() {

            @Override
            public void onPrepare(long allMilliseconds) {
                mAllMilliseconds = allMilliseconds;
            }

            @Override
            public void onPlay(boolean enablePlay, long allMilliseconds) {
                mIvControlP.setSelected(enablePlay);
                mIvControlL.setSelected(!enablePlay);
                mAllMilliseconds = allMilliseconds;
            }

            @Override
            public void onProgress(long currMilliseconds) {
                int progress = (int) ((float) currMilliseconds * 100 / mAllMilliseconds);
                mSb.setProgress(progress);
                mLayoutCp.setProgress(progress);
                String time = Time.secondFormat(currMilliseconds / 1000, DateUnit.minute);
                mTvTimeP.setText(time);
                mTvTimeL.setText(time);
            }

            @Override
            public void onStop() {
                mIvControlP.setSelected(false);
                mIvControlL.setSelected(true);
            }

            @Override
            public void onClick() {
                if (!mIsPortrait) {
                    // 横屏
                    if (mLayoutL.getVisibility() == View.VISIBLE) {
                        goneView(mLayoutL);
                        goneView(getNavBar());
                        mCountDown.stop();
                    } else {
                        showView(mLayoutL);
                        showView(getNavBar());
                        countDown();
                    }
                }
            }

            @Override
            public void end() {
                mSb.setProgress(100);
                mLayoutCp.setProgress(100);
                mIvControlP.setSelected(false);
                mIvControlL.setSelected(true);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPPT != null) {
            resetPlayStatus();
            getItem(mLastPosition).stop();
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundColor(Color.TRANSPARENT);

        bar.addViewLeft(R.mipmap.nav_bar_ic_back, v -> {
            if (mIsPortrait) {
                finish();
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 切换为竖屏
                Observable.just((Runnable) () ->
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)) // 设置回默认值
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribe(Runnable::run);
            }
        });

        mLayoutBarMid = inflate(R.layout.layout_meeting_nav_bar_course);
        mTvSelect = (TextView) mLayoutBarMid.findViewById(R.id.meeting_nav_bar_select);
        mTvAll = (TextView) mLayoutBarMid.findViewById(R.id.meeting_nav_bar_all);
        bar.addViewMid(mLayoutBarMid);

        mLayoutBarRight = bar.addViewRight(R.mipmap.meeting_ppt_ic_record, v -> {
            statsStudy();
            MeetingRecordActivity.nav(MeetingCourseActivity.this, mPPT, getCurrentItem());
        });
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTimeP = findView(R.id.meeting_ppt_tv_time_p);
        mTvTimeL = findView(R.id.meeting_ppt_tv_time_l);
        mIvControlP = findView(R.id.meeting_ppt_iv_control_p);
        mIvControlL = findView(R.id.meeting_ppt_iv_control_l);

        mSb = findView(R.id.meeting_ppt_sb_progress);

        mLayoutBar = findView(R.id.meeting_ppt_view);
        mLayoutCp = findView(R.id.meeting_ppt_layout_progress);
        mLayoutControl = findView(R.id.meeting_ppt_layout_control);
        mLayoutP = findView(R.id.meeting_ppt_layout_p);
        mLayoutL = findView(R.id.meeting_ppt_layout_l);
    }

    @Override
    public void setViews() {
        super.setViews();

        mParams = getViewPager().getLayoutParams(); // viewPager的布局参数
        mIsPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        touch(mIvControlL);
        touch(mLayoutL);
        touch(mTvTimeL);

        mSb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (mCourses.get(getCurrentItem()).getType()) {
                    case CourseType.pic_audio:
                    case CourseType.video:
                    case CourseType.audio: {
                        mTvTimeL.setText(Time.secondFormat((long) (mSb.getProgress() / 100.0 * mAllMilliseconds / 1000), DateUnit.minute));
                    }
                    break;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mCountDown != null) {
                    mCountDown.stop();
                }
                switch (mCourses.get(getCurrentItem()).getType()) {
                    case CourseType.pic_audio:
                    case CourseType.audio:
                    case CourseType.video:
                        // 有音频或视频的时候
                        getItem(getCurrentItem()).pause();
                        break;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                switch (mCourses.get(getCurrentItem()).getType()) {
                    case CourseType.pic_audio:
                    case CourseType.audio:
                    case CourseType.video: {
                        BaseCourseFrag frag = getItem(getCurrentItem());
                        int playTime = (int) (mSb.getProgress() / 100f * mAllMilliseconds);
                        frag.seekTo(playTime);
                        frag.setRemainTime((int) (mAllMilliseconds - playTime) / 1000);
                    }
                    break;
                }
                countDown();
            }
        });

        setOnClickListener(R.id.meeting_ppt_iv_left);
        setOnClickListener(R.id.meeting_ppt_iv_right);
        setOnClickListener(R.id.meeting_ppt_iv_control_p);
        setOnClickListener(R.id.meeting_ppt_iv_control_l);
        setOnClickListener(R.id.meeting_ppt_iv_first);
        setOnClickListener(R.id.meeting_ppt_iv_comment);
        setOnClickListener(R.id.meeting_ppt_layout_control);

        setOffscreenPageLimit(KVpSize);
        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                statsStudy();

                setPlayStatus(position);
                setStatus(position);

                NetworkImageView.clearMemoryCache(MeetingCourseActivity.this);

                mLastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.toPPT(mMeetId, mModuleId));
    }

    private void setPlayStatus(int position) {
        if (position != mLastPosition) {
            getItem(mLastPosition).stop();
        }
        // FIXME: 暂时无条件播放
        getItem(position).start();
    }

    private void setStatus(int position) {
        // 切换viewPager改变提示
        mTvSelect.setText(String.valueOf(position + 1));
        mTvTimeP.setText("加载中");
        mLayoutCp.setProgress(0);

        Course item = mCourses.get(position);
        switch (item.getType()) {
            case CourseType.pic_audio:
            case CourseType.audio:
            case CourseType.video: {
                // 有音频的时候
                showView(mLayoutControl);
                showView(mTvTimeP);
            }
            break;
            default: {
                // 没有音频的时候
                hideView(mLayoutControl);
                hideView(mTvTimeP);
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), PPT.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result<PPT> r = (Result<PPT>) result;
        if (r.isSucceed()) {
            mPPT = r.getData();
            CourseInfo courseInfo = mPPT.getEv(TPPT.course);
            if (courseInfo != null) {
                mCourses = courseInfo.getList(TCourseInfo.details);
                mTvAll.setText(String.valueOf(mCourses.size()));
                if (mCourses != null && mCourses.size() > 0) {
                    switch (mCourses.get(0).getType()) {
                        case CourseType.pic_audio:
                        case CourseType.audio:
                        case CourseType.video: {
                            // 有音频的时候
                            showView(mLayoutControl);
                            showView(mTvTimeP);
                        }
                        break;
                        default:
                            // 没有音频的时候
                            hideView(mLayoutControl);
                            hideView(mTvTimeP);
                            break;
                    }
                    // 初始显示
                    mTvSelect.setText("1");
                    // 逐个添加Frag
                    for (Course course : mCourses) {
                        addPPTFrag(course);
                    }
                    invalidate();
                }
            } else {

            }
        } else {
            showToast(r.getError());
        }
    }

    /**
     * 保存停留时间
     */
    private void statsStudy() {
        long curTime = System.currentTimeMillis();
        Long study = mTimes.get(mLastPosition); // 本来记录的时间
        if (study != null) {
            // 有记录加上
            curTime += study;
        }
        mTimes.put(mLastPosition, curTime - mStartTime);
        mStartTime = System.currentTimeMillis(); // 下一页的开始时间
    }

    /**
     * 根据返回的url确定添加的frag的类型
     * 优先判断video
     *
     * @param course
     * @return
     */
    private void addPPTFrag(Course course) {
        BaseCourseFrag frag = null;

        switch (course.getType()) {
            case CourseType.audio: {
                frag = new AudioCourseFrag();
            }
            break;
            case CourseType.video: {
                frag = new VideoCourseFrag();
            }
            break;

            case CourseType.pic: {
                frag = new PicCourseFrag();
            }
            break;

            case CourseType.pic_audio: {
                frag = new PicAudioCourseFrag();
            }
            break;
        }

        Bundle b = new Bundle();
        b.putString(Extra.KMeetId, mMeetId);
        b.putSerializable(Extra.KData, course);

        if (frag != null) {
            frag.setArguments(b);
            frag.setOnPPTListener(mListener);
            add(frag);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_ppt_iv_left: {
                // 上一页
                if (mCourses == null) {
                    break;
                }

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
                if (mCourses == null) {
                    break;
                }

                int currNext = getCurrentItem() + 1;
                if (currNext < mCourses.size()) {
                    setCurrentItem(currNext);
                } else {
                    showToast("已是最后一页");
                }
            }
            break;
            case R.id.meeting_ppt_iv_control_p:
            case R.id.meeting_ppt_iv_control_l: {
                // 控制
                getItem(getCurrentItem()).toggle();
            }
            break;
            case R.id.meeting_ppt_iv_first:
                // 切换到第一页
                setCurrentItem(0);
                break;
            case R.id.meeting_ppt_iv_comment:
                // 评论
                statsStudy();
                MeetingCommentActivity.nav(MeetingCourseActivity.this, mMeetId);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
            mIsPortrait = true;
            mParams.height = fitDp(KViewPagerHDp);
            getViewPager().setLayoutParams(mParams);

            showView(mLayoutP);
            goneView(mLayoutL);
            showView(mLayoutBar);
            showView(mLayoutBarMid);
            showView(mLayoutBarRight);
            showView(getNavBar());
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            mIsPortrait = false;
            mParams.height = MATCH_PARENT;
            getViewPager().setLayoutParams(mParams);

            showView(mLayoutL);
            goneView(mLayoutP);
            goneView(mLayoutBar);
            goneView(mLayoutBarMid);
            goneView(mLayoutBarRight);

            showView(getNavBar());
            countDown();
        }
    }

    @Override
    protected BaseCourseFrag getItem(int position) {
        return (BaseCourseFrag) super.getItem(position);
    }

    /**
     * 控件按下取消倒数消失
     *
     * @param view
     */
    private void touch(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (mCountDown != null) {
                        mCountDown.stop();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    countDown();
                    break;
            }
            return false;
        });
    }

    /**
     * 倒计时
     */
    private void countDown() {
        if (mCountDown == null) {
            mCountDown = new CountDown(KVanishTime);
            mCountDown.setListener(MeetingCourseActivity.this);
        }
        mCountDown.start();
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            if (!mIsPortrait) {
                goneView(getNavBar());
                goneView(mLayoutL);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();

        statsStudy();

        notify(NotifyType.study_end);

        // 拼接需要的数据
        JSONArray ja = new JSONArray();
        try {
            JSONObject jsonObject;
            long studyTime;
            for (Integer key : mTimes.keySet()) {
                jsonObject = new JSONObject();
                jsonObject.put(MeetParam.KDetailId, mCourses.get(key).getLong(TCourse.id));
                studyTime = mTimes.get(key) / TimeUnit.SECONDS.toMillis(1);
                jsonObject.put(MeetParam.KUseTime, studyTime == 0 ? 1 : studyTime); // 至少传1秒
                jsonObject.put(MeetParam.KFinish, getItem(key).isFinish());
                ja.put(jsonObject);
            }
        } catch (JSONException e) {
            YSLog.d(TAG, "onDestroy:" + e.toString());
        }
        Submit submit = new Submit();
        submit.put(TSubmit.meetId, mPPT.getString(TPPT.meetId));
        submit.put(TSubmit.moduleId, mPPT.getString(TPPT.moduleId));
        submit.put(TSubmit.courseId, mPPT.getString(TPPT.courseId));
        submit.put(TSubmit.times, ja.toString());

        // 把需要的对象传给服务提交(失败再次提交)
        Intent intent = new Intent(this, CommonServ.class)
                .putExtra(Extra.KType, ReqType.course)
                .putExtra(Extra.KData, submit);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCountDown != null) {
            mCountDown.stop();
            mCountDown = null;
        }
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

    private void resetPlayStatus() {
        mLayoutCp.setProgress(0);
        // 同一页的起始状态
        mIvControlL.setSelected(true);
        mIvControlP.setSelected(false);
        mTvTimeL.setText("00:00");
        mTvTimeP.setText("00:00");
    }
}