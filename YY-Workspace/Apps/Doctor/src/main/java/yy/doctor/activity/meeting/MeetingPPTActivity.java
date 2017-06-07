package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseVPActivity;
import lib.yy.network.Result;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.meeting.BaseMeetingPPTFrag;
import yy.doctor.frag.meeting.MeetingPPTPicFrag;
import yy.doctor.frag.meeting.MeetingPPTPicFrag.OnPPTPicListener;
import yy.doctor.frag.meeting.MeetingPPTVideoFrag;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.Detail;
import yy.doctor.model.meet.Detail.TDetail;
import yy.doctor.model.meet.Ppt;
import yy.doctor.model.meet.Ppt.TPpt;
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

public class MeetingPPTActivity extends BaseVPActivity implements OnPPTPicListener {

    private static final int KVpSize = 3;
    private final int KViewPagerHDp = 271;

    private String mMeetId;
    private String mModuleId;
    private Ppt mPpt;
    private boolean mIsPortrait; // 是否为竖屏
    private List<Detail> mDetails; // ppt的内容

    private CircleProgressView mView;
    private TextView mTvBarMid; // NavBar中间的提示
    private TextView mTvTime; // 音频/视频播放的时间
    private View mControl; //
    private ViewGroup.LayoutParams mParams;
    private View mLayoutLandscape; // 横屏布局
    private View mLayoutPortrait; // 竖屏布局
    private View mBarView; // 占位图
    private ImageView mIvControl;
    private View mTvBarRight;

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

        mTvBarMid = new TextView(this);

        bar.addViewMid(mTvBarMid);
        mTvBarRight = bar.addViewRight(R.mipmap.meeting_ppt_ic_record, v -> MeetingRecordActivity.nav(MeetingPPTActivity.this, mPpt));
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void findViews() {
        super.findViews();
        mView = findView(R.id.meeting_ppt_v_progress);
        mControl = findView(R.id.meeting_ppt_layout_control);
        mTvTime = findView(R.id.meeting_ppt_tv_time);
        mBarView = findView(R.id.meeting_ppt_view);
        mIvControl = findView(R.id.meeting_ppt_iv_control);
        mLayoutLandscape = findView(R.id.video_layout_function);
        mLayoutPortrait = findView(R.id.meeting_ppt_layout_portrait);
        mParams = getViewPager().getLayoutParams(); // viewPager的布局参数
    }

    @Override
    public void setViews() {
        super.setViews();

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
                mTvBarMid.setText(position + 1 + "/" + mDetails.size());
                mTvTime.setText("加载中");
                mView.setProgress(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.toPpt(mMeetId, mModuleId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Ppt.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result<Ppt> r = (Result<Ppt>) result;
        if (r.isSucceed()) {
            mPpt = r.getData();
            Course course = mPpt.getEv(TPpt.course);
            mDetails = course.getList(TCourse.details);
            // 初始显示
            if (mDetails.size() > 0) {
                mTvBarMid.setText("1/" + mDetails.size());
            } else {
                mTvBarMid.setText("0/" + mDetails.size());
            }
            // 逐个添加Frag
            BaseMeetingPPTFrag frag;
            for (Detail detail : mDetails) {
                frag = getPPTFrag(detail);
                frag.setDetail(detail);
                frag.setMeetId(mMeetId);
                add(frag);
            }
            invalidate();
        }
    }

    /**
     * 根据返回的url确定添加的frag的类型
     * 优先判断video
     *
     * @param detail
     * @return
     */
    @NonNull
    private BaseMeetingPPTFrag getPPTFrag(Detail detail) {
        if (TextUtil.isEmpty(detail.getString(TDetail.videoUrl))) {
            MeetingPPTPicFrag picFrag = new MeetingPPTPicFrag();
            picFrag.setOnPPTPicListener(this);
            return picFrag;
        } else {
            MeetingPPTVideoFrag videoFrag = new MeetingPPTVideoFrag();
            return videoFrag;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_ppt_iv_left: // 上一页
                int currPro = getCurrentItem() - 1;
                if (mDetails != null && currPro >= 0) {
                    setCurrentItem(currPro);
                }
                break;
            case R.id.meeting_ppt_iv_right: // 下一页
                int currNext = getCurrentItem() + 1;
                if (mDetails != null && currNext < mDetails.size()) {
                    setCurrentItem(currNext);
                }
                break;
            case R.id.meeting_ppt_iv_control: // 控制
                BaseMeetingPPTFrag pptFrag = (BaseMeetingPPTFrag) getData().get(getCurrentItem());
                mIvControl.setSelected(!mIvControl.isSelected());
                pptFrag.toggle();
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
    public void OnAudio(boolean has, int all) {
        if (has) { // 有音频的时候
            showView(mControl);
            showView(mTvTime);
            mView.setMaxProgress(all);
            mIvControl.setSelected(true);
        } else { // 没有音频的时候
            goneView(mControl);
            goneView(mTvTime);
        }
    }

    @Override
    public void OnPlay(long time) {
        mTvTime.setText(Util.formatTime(time / 1000, DateUnit.minute));
        mView.setProgress((int) time);
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
            showView(mBarView);
            showView(mTvBarMid);
            showView(mTvBarRight);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            mIsPortrait = false;
            mParams.height = MATCH_PARENT;
            getViewPager().setLayoutParams(mParams);

            showView(mLayoutLandscape);
            goneView(mLayoutPortrait);
            goneView(mBarView);
            goneView(mTvBarMid);
            goneView(mTvBarRight);
        }
    }

}
