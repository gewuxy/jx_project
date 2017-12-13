package jx.doctor.ui.frag.meeting;

import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Route;
import lib.ys.ConstantsEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.jx.ui.frag.base.BaseVPFrag;
import jx.doctor.R;
import jx.doctor.model.meet.Submit;
import jx.doctor.model.meet.Submit.TSubmit;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.CourseType;
import jx.doctor.model.meet.ppt.CourseInfo;
import jx.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.model.meet.ppt.PPT.TPPT;
import jx.doctor.serv.CommonServ;
import jx.doctor.serv.CommonServRouter;
import jx.doctor.ui.frag.meeting.course.AudioCourseFragRouter;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag.OnFragClickListener;
import jx.doctor.ui.frag.meeting.course.PicAudioCourseFrag;
import jx.doctor.ui.frag.meeting.course.PicAudioCourseFragRouter;
import jx.doctor.ui.frag.meeting.course.PicCourseFragRouter;
import jx.doctor.ui.frag.meeting.course.VideoCourseFrag;
import jx.doctor.ui.frag.meeting.course.VideoCourseFragRouter;
import jx.doctor.util.NetPlayer;
import jx.doctor.util.Util;

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

    private View mLayoutNew;
    private TextView mTvNew;
    private View mLayoutMedia;
    private ImageView mIvMedia;
    private TextView mTvMedia;
    private View mLayoutL;

    private Handler mHandler;
    private String mUrl;

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

    /**
     * 是否拦截触摸事件
     */
    public void setDispatch(boolean dispatch) {
        mDispatch = dispatch;
    }

    @Override
    public void initData() {
        mLastPosition = 0;
        mDispatch = false;
        mSubmits = new HashMap<>();

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                newVisibility(false);
            }

        };
    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_ppt_reb;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutNew = findView(R.id.ppt_layout);
        mTvNew = findView(R.id.ppt_tv_num);

        mLayoutMedia = findView(R.id.ppt_layout_media);
        mIvMedia = findView(R.id.ppt_iv_media);
        mTvMedia = findView(R.id.ppt_tv_media);
        mLayoutL = findView(R.id.ppt_layout_landscape);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);
        setOnPageChangeListener(this);
        setOnClickListener(R.id.ppt_layout);
        setOnClickListener(R.id.ppt_iv_left_landscape);
        setOnClickListener(R.id.ppt_iv_right_landscape);
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

        mediaVisibility(false);
        mTvMedia.setText("加载中");

//        NetworkImageView.clearMemoryCache(getContext());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }

    @Override
    public void onClick() {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ppt_layout: {
                setToLastPosition();
                newVisibility(false);
            }
            break;
            case R.id.ppt_iv_left_landscape: {
                offsetPosition(-1, getString(R.string.course_first));
            }
            break;
            case R.id.ppt_iv_right_landscape: {
                offsetPosition(1, getString(R.string.course_last));
            }
            break;
        }
    }

    /**
     * 追加新的ppt
     */
    public void addCourse(Course course) {
        if (course == null) {
            return;
        }
        BaseCourseFrag f = getPPTFrag(course);
        mCourses.add(course);
        if (f != null) {
            f.setListener(this);
            add(f);
            invalidate();
        }
    }

    /**
     * 删除ppt
     *
     * @param course 下标
     */
    public void removeCourse(Course course) {
        if (mCourses == null || course == null) {
            return;
        }
        mCourses.remove(course);
        getAdapter().removeAll();
        invalidate();
        addCourses();
    }

    /**
     * 添加原有ppt
     */
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
    public int getCurrPosition() {
        return super.getCurrPosition();
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
    public void setCurrPosition(int position) {
        super.setCurrPosition(position);
    }

    public void offsetPosition(int offset, String content) {
        int position = getCurrPosition() + offset;
        if (position >= 0 && position <= getCount() - 1) {
            setCurrPosition(position);
        } else {
            showToast(content);
        }
    }

    public void setToLastPosition() {
        if (mCourses != null) {
            setCurrPosition(mCourses.size() - 1);
        }
    }

    /**
     * 当前ppt缩放至原来
     */
    public void refreshCurrentItem() {
        BaseCourseFrag item = getItem(getCurrPosition());
        if (item instanceof PicAudioCourseFrag) {
            PicAudioCourseFrag f = (PicAudioCourseFrag) item;
            f.setScale();
        }
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        BaseCourseFrag frag = getItem(getCurrPosition());
        if (frag instanceof VideoCourseFrag) {
            VideoCourseFrag f = (VideoCourseFrag) frag;
            NetPlayer.inst().setVideo(f.getTextureView());
        } else {
            NetPlayer.inst().setAudio();
        }
        String url = frag.getUrl();
        if (TextUtil.isEmpty(url)) {
            // FIXME: 什么时候显示录音中
            setTextMedia("录音中");
        } else {
            if (url.equals(mUrl) && NetPlayer.inst().isPlaying()) {
                return;
            }
            mUrl = url;
            NetPlayer.inst().stop();
            NetPlayer.inst().prepare(mPPT.getString(TPPT.meetId), mUrl);
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        NetPlayer.inst().stop();
        mUrl = ConstantsEx.KEmpty;
    }

    public boolean startVolume() {
        return NetPlayer.inst().openVolume();
    }

    public boolean closeVolume() {
        return NetPlayer.inst().closeVolume();
    }

    /**
     * 记录上一页的学习时间
     */
    public void saveStudyTime() {
        if (mLastPosition < 0 || mLastPosition >= getCount()) {
            return;
        }
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

    public void setTextNew(CharSequence c) {
        mTvNew.setText(c);
        newVisibility(true);
    }

    public void setTextMedia(CharSequence c) {
        mTvMedia.setText(c);
        mediaVisibility(true);
    }

    public void animation(boolean state) {
        if (state) {
            // 开启音频的时候需要显示(暂停不干涉)
            mediaVisibility(true);
        }
        Drawable drawable = mIvMedia.getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) drawable;
            if (state == animation.isRunning()) {
                return;
            }
            if (state) {
                animation.start();
            } else {
                animation.stop();
            }
        }
    }

    /**
     * 横屏左右按钮
     */
    public void landscapeVisibility(boolean visibility) {
        viewVisibility(mLayoutL, visibility);
    }

    /**
     * 右上角最新页
     */
    public void newVisibility(boolean visibility) {
        mHandler.removeMessages(Integer.MAX_VALUE);
        viewVisibility(mLayoutNew, visibility);
        mHandler.sendEmptyMessageDelayed(Integer.MAX_VALUE, TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * 右下角音频动画
     */
    public void mediaVisibility(boolean visibility) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT || !visibility) {
            // 竖屏或者需要隐藏的时候
            viewVisibility(mLayoutMedia, visibility);
        }
    }

    private void viewVisibility(View v, boolean visibility) {
        if (visibility) {
            showView(v);
        } else {
            goneView(v);
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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDispatch) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
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

        mHandler.removeCallbacksAndMessages(null);
    }
}
