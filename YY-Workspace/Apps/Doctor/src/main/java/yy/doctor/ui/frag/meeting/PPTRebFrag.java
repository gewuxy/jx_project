package yy.doctor.ui.frag.meeting;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseVPFrag;
import yy.doctor.R;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.CourseType;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
import yy.doctor.serv.CommonServ;
import yy.doctor.serv.CommonServRouter;
import yy.doctor.ui.frag.meeting.course.AudioCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag.OnFragClickListener;
import yy.doctor.ui.frag.meeting.course.PicAudioCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.PicCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.VideoCourseFrag;
import yy.doctor.ui.frag.meeting.course.VideoCourseFragRouter;
import yy.doctor.util.NetPlayer;
import yy.doctor.util.Util;

/**
 * PPT部分
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class PPTRebFrag extends BaseVPFrag implements OnPageChangeListener, OnFragClickListener {

    private final int KVpSize = 3; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长

    private PPT mPPT;
    private int mLastPosition; // 上一次的position
    private long mStartTime; // 开始时间
    private HashMap<Integer, Submit> mSubmits; // 学习时间
    private List<Course> mCourses;

    private OnFragClickListener mListener;
    private boolean mDispatch; // 是否处理触摸事件

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        setOnPageChangeListener(listener); // 外部添加
    }

    public void setFragClickListener(OnFragClickListener onFragClickListener) {
        mListener = onFragClickListener;
    }

    public void setPPT(PPT ppt) {
        mPPT = ppt;

        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void initData() {
        mLastPosition = 0;
        mDispatch = false;
        mSubmits = new HashMap<>();
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_ppt_viewpager;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);
        setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing
    }

    @Override
    public void onPageSelected(int position) {
        Util.noNetwork(); // 图片加载需要检查网络

        saveStudyTime();

        mLastPosition = position;

//        NetworkImageView.clearMemoryCache(getContext());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }

    /**
     * 记录上一页的学习时间
     */
    public void saveStudyTime() {
        Submit submit = mSubmits.get(mLastPosition);
        if (submit == null) {
            if (getItem(mLastPosition) == null) {
                return; // 未初始化成功
            }
            submit = getItem(mLastPosition).getSubmit();
            mSubmits.put(Integer.valueOf(mLastPosition), submit);
        }
        long studyTime = submit.getLong(TSubmit.usedtime, 0);
        long curTime = System.currentTimeMillis();
        // 加上原来记录
        curTime += studyTime - mStartTime;
        submit.put(TSubmit.usedtime, curTime);
        mStartTime = System.currentTimeMillis();
    }

    /**
     * 根据类型添加的frag
     */
    @Nullable
    private BaseCourseFrag getPPTFrag(Course course) {
        BaseCourseFrag frag = null;
        String meetId = mPPT.getString(TPPT.meetId);
        switch (course.getType()) {
            case CourseType.audio: {
                frag = AudioCourseFragRouter.create(course, meetId).route();
            }
            break;
            case CourseType.video: {
                frag = VideoCourseFragRouter.create(course, meetId).route();
            }
            break;
            case CourseType.pic: {
                frag = PicCourseFragRouter.create(course, meetId).route();
            }
            break;
            case CourseType.pic_audio: {
                frag = PicAudioCourseFragRouter.create(course, meetId).route();
            }
            break;
        }
        return frag;
    }

    public void addCourse(Course course) {
        BaseCourseFrag f = getPPTFrag(course);
        mCourses.add(course);
        if (f != null) {
            f.setListener(this);
            add(f);
            invalidate();
        }
    }

    public void addCourses() {
        if (mPPT == null) {
            return;
        }

        CourseInfo courseInfo = mPPT.get(TPPT.course);
        if (courseInfo == null) {
            return;
        }

        mCourses = courseInfo.getList(TCourseInfo.details);
        if (mCourses == null || mCourses.size() == 0) {
            return;
        }
        // 逐个添加Frag
        for (Course course : mCourses) {
            BaseCourseFrag f = getPPTFrag(course);
            if (f != null) {
                f.setListener(this);
                add(f);
            }
        }
        invalidate();
    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public BaseCourseFrag getItem(int position) {
        return (BaseCourseFrag) super.getItem(position);
    }

    public void setCurrentItem() {
        if (mCourses != null) {
            setCurrentItem(mCourses.size());
        }
    }

    @Override
    public void setCurrentItem(int position) {
        super.setCurrentItem(position);
    }

    public void setCurrentItem(int offset, String content) {
        int position = getCurrentItem() + offset;
        if (position >= 0 || position <= getCount() - 1) {
            setCurrentItem(position);
        } else {
            showToast(content);
        }
    }

    @Override
    public void onDestroy() {
        saveStudyTime();

        // 把需要的对象传给服务提交(失败再次提交)
        CommonServRouter.create()
                .type(CommonServ.ReqType.course)
                .submit(getSubmit(mSubmits))
                .route(getContext());

        // 保持调用顺序
        super.onDestroy();
    }

    public Submit getSubmit(Map<Integer, Submit> p) {
        if (mSubmits.isEmpty()) {
            return null;
        }
        Submit submit = null;
        JSONArray ja = new JSONArray();
        long studyTime;
        for (int i = 0; i < p.size(); i++) {
            Submit s = p.get(i);
            if (s == null) {
                continue;
            }
            studyTime = s.getLong(TSubmit.usedtime, 0) / TimeUnit.SECONDS.toMillis(1);
            s.put(TSubmit.usedtime, studyTime == 0 ? 1 : studyTime); // 至少传1秒
            ja.put(s.toJsonObject());
            if (submit == null) {
                submit = new Submit();
                submit.put(TSubmit.meetId, mPPT.getString(TPPT.meetId));
                submit.put(TSubmit.moduleId, mPPT.getString(TPPT.moduleId));
                submit.put(TSubmit.courseId, mPPT.getString(TPPT.courseId));
            }
        }
        if (submit != null) {
            submit.put(TSubmit.times, ja.toString());
        }

        return submit;
    }

    @Override
    public void onClick() {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    public void startPlay() {
        NetPlayer.inst().stop();
        BaseCourseFrag frag = getItem(getCurrentItem());
        if (frag instanceof VideoCourseFrag) {
            VideoCourseFrag f = (VideoCourseFrag) frag;
            NetPlayer.inst().setVideo(f.getTextureView());
        } else {
            NetPlayer.inst().setAudio();
        }
        NetPlayer.inst().prepare(mPPT.getString(TPPT.meetId), frag.getUrl());
    }

    public void stopPlay() {
        NetPlayer.inst().stop();
    }

    public void setDispatch(boolean dispatch) {
        mDispatch = dispatch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDispatch) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}
