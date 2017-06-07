package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseVPActivity;
import lib.yy.network.Result;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.meeting.course.BaseCourseFrag;
import yy.doctor.frag.meeting.course.BaseCourseFrag.OnPPTListener;
import yy.doctor.frag.meeting.course.PicAudioCourseFrag;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * 观看会议界面
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */

public class MeetingPPTActivity extends BaseVPActivity {

    private static final int KVpSize = 3;
    private final int KViewPagerHDp = 271;

    private String mMeetId;
    private String mModuleId;
    private PPT mPPT;
    private boolean mIsPortrait; // 是否为竖屏
    private List<Course> mCourses; // ppt的内容

    private ViewGroup.LayoutParams mParams;

    private CircleProgressView mLayoutCp;

    private TextView mTvBarMid; // NavBar中间的提示
    private TextView mTvTime; // 音频/视频播放的时间

    private View mLayoutBarRight;
    private View mLayoutControl; //
    private View mLayoutLandscape; // 横屏布局
    private View mLayoutPortrait; // 竖屏布局
    private View mLayoutBar; // 占位图

    private ImageView mIvControl;

    private OnPPTListener mListener;


    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, MeetingPPTActivity.class)
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
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);

        mListener = new OnPPTListener() {

            @Override
            public void onPrepare(boolean enablePlay) {
                if (enablePlay) { // 有音频的时候
                    showView(mLayoutControl);
                    showView(mTvTime);
//            mLayoutCp.setMaxProgress(all);
                } else { // 没有音频的时候
                    goneView(mLayoutControl);
                    goneView(mTvTime);
                }
            }

            @Override
            public void onStart(boolean enablePlay) {
                mIvControl.setSelected(enablePlay);
            }

            @Override
            public void onProgress(long currSeconds) {
                mTvTime.setText(Util.formatTime(currSeconds / 1000, DateUnit.minute));
                mLayoutCp.setProgress((int) currSeconds);
            }

            @Override
            public void onStop() {
            }
        };
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

        // TODO: 临时布局
        mTvBarMid = new TextView(this);
        bar.addViewMid(mTvBarMid);

        mLayoutBarRight = bar.addViewRight(R.mipmap.meeting_ppt_ic_record, v -> MeetingRecordActivity.nav(MeetingPPTActivity.this, mPPT));
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutCp = findView(R.id.meeting_ppt_layout_progress);
        mLayoutControl = findView(R.id.meeting_ppt_layout_control);
        mTvTime = findView(R.id.meeting_ppt_tv_time);
        mLayoutBar = findView(R.id.meeting_ppt_view);
        mIvControl = findView(R.id.meeting_ppt_iv_control);
        mLayoutLandscape = findView(R.id.video_layout_function);
        mLayoutPortrait = findView(R.id.meeting_ppt_layout_portrait);
    }

    @Override
    public void setViews() {
        super.setViews();

        mParams = getViewPager().getLayoutParams(); // viewPager的布局参数

        mIsPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        setOnClickListener(R.id.meeting_ppt_iv_left);
        setOnClickListener(R.id.meeting_ppt_iv_right);
        setOnClickListener(R.id.meeting_ppt_iv_control);
        setOnClickListener(R.id.meeting_ppt_iv_first);
        setOnClickListener(R.id.meeting_ppt_iv_comment);
        setOnClickListener(R.id.meeting_ppt_layout_control);

        setOffscreenPageLimit(KVpSize);
        setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) { // 切换viewPager改变中间的提示
                mTvBarMid.setText(position + 1 + "/" + mCourses.size());
                mTvTime.setText("加载中");
                mLayoutCp.setProgress(0);

                mCourses.get(position).getType();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.toPPT(mMeetId, mModuleId));
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
            mCourses = courseInfo.getList(TCourseInfo.details);
            // 初始显示
            if (mCourses.size() > 0) {
                mTvBarMid.setText("1/" + mCourses.size());
            } else {
                mTvBarMid.setText("0/" + mCourses.size());
            }
            // 逐个添加Frag
            for (Course course : mCourses) {
                addPPTFrag(course);
            }
            invalidate();
        }
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
//                frag = new audioc
            }
            break;
            case CourseType.pic: {

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
        frag.setArguments(b);

        frag.setOnPPTListener(mListener);

        if (frag != null) {
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
                    // FIXME: 提示语
                    showToast("已经是首页");
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
                    // FIXME: 提示语
                    showToast("到头了");
                }
            }
            break;
            case R.id.meeting_ppt_iv_control: {
                // 控制
                BaseCourseFrag pptFrag = getItem(getCurrentItem());
                mIvControl.setSelected(!mIvControl.isSelected());
                // TODO: 加入toggle listener
                pptFrag.toggle();
            }
            break;
            case R.id.meeting_ppt_iv_first: // 第一页
                setCurrentItem(0);
                break;
            case R.id.meeting_ppt_iv_comment: // 评论
                MeetingCommentActivity.nav(MeetingPPTActivity.this, mMeetId);
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

            showView(mLayoutPortrait);
            goneView(mLayoutLandscape);
            showView(mLayoutBar);
            showView(mTvBarMid);
            showView(mLayoutBarRight);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            mIsPortrait = false;
            mParams.height = MATCH_PARENT;
            getViewPager().setLayoutParams(mParams);

            showView(mLayoutLandscape);
            goneView(mLayoutPortrait);
            goneView(mLayoutBar);
            goneView(mTvBarMid);
            goneView(mLayoutBarRight);
        }
    }

    @Override
    protected BaseCourseFrag getItem(int position) {
        return (BaseCourseFrag) super.getItem(position);
    }

}
