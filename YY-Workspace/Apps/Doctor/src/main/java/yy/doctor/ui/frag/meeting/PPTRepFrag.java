package yy.doctor.ui.frag.meeting;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseVPFrag;
import yy.doctor.R;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
import yy.doctor.serv.CommonServ;
import yy.doctor.serv.CommonServRouter;
import yy.doctor.ui.frag.meeting.course.AudioCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag.OnFragClickListener;
import yy.doctor.ui.frag.meeting.course.PicAudioCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.PicCourseFragRouter;
import yy.doctor.ui.frag.meeting.course.VideoCourseFragRouter;
import yy.doctor.util.Util;

/**
 * PPT部分
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class PPTRepFrag extends BaseVPFrag implements OnPageChangeListener, OnFragClickListener {

    private final int KVpSize = 3; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长

    private PPT mPPT;
    private int mLastPosition; // 上一次的position
    private long mStartTime; // 开始时间
    private HashMap<Integer, Submit> mSubmits; // 学习时间

    private OnFragClickListener mListener;

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        setOnPageChangeListener(listener); // 外部添加
    }

    public void setFragClickListener(OnFragClickListener listener) {
        mListener = listener;
    }

    public void setPPT(PPT ppt) {
        if (ppt == null || mPPT != null) {
            // 不重复设置
            return;
        }
        mPPT = ppt;

        CourseInfo courseInfo = mPPT.get(TPPT.course);
        if (courseInfo == null) {
            return;
        }

        List<Course> courses = courseInfo.getList(TCourseInfo.details);
        if (courses == null || courses.size() == 0) {
            return;
        }
        // 逐个添加Frag
        for (Course course : courses) {
            BaseCourseFrag f = getPPTFrag(course);
            if (f != null) {
                f.setListener(this);
                add(f);
            }
        }
        invalidate();

        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void initData() {
        mLastPosition = 0;
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
        long studyTime = submit.getLong(Submit.TSubmit.usedtime, 0);
        long curTime = System.currentTimeMillis();
        // 加上原来记录
        curTime += studyTime - mStartTime;
        submit.put(Submit.TSubmit.usedtime, curTime);
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void onClick() {
        if (mListener != null) {
            mListener.onClick();
        }
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

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
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
}
