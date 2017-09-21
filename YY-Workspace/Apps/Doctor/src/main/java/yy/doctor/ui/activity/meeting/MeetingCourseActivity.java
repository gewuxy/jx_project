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
import lib.ys.ConstantsEx;
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
        OnPageChangeListener, OnFragClickListener {

    private final int KVpSize = 3; // Vp缓存的数量
    private final int KVanishTime = 3; // 横屏显示时间

    @Arg
    String mMeetId; // 会议ID

    @Arg
    String mModuleId; // 模块ID


    private long mStartTime; // 开始时间
    private PPT mPPT;
    private List<Course> mCourses;
    private int mLastPosition; // 上一次的position
    private Map<Integer, Submit> mSubmits; // 学习时间

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mLastPosition = 0;
        mSubmits = new HashMap<>();
    }

    @Override
    public void initNavBar(NavBar bar) {

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


    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(KVpSize);
        setOnPageChangeListener(this);

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

        mLastPosition = position;
        NetworkImageView.clearMemoryCache(MeetingCourseActivity.this);
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

    }

    /**
     * 切换PPT是页面重置
     */
    private void setStatus(int position) {
        mStartTime = System.currentTimeMillis();
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

            // 逐个添加Frag
            for (Course course : mCourses) {
                addPPTFrag(course);
            }

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 初始显示
                    mStartTime = System.currentTimeMillis();
                    setStatus(0);
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
    public void onClick() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏

        }
    }

    @Override
    protected void onDestroy() {
        saveStudyTime();

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

}