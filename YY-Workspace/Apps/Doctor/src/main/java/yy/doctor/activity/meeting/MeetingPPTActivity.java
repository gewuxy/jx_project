package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import lib.ys.activity.ActivityEx;
import lib.ys.decor.DecorViewEx.TNavBarState;
import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import lib.ys.util.ToastUtil;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 观看会议界面
 *
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class MeetingPPTActivity extends ActivityEx {

    private NetworkImageView mNivPlay;

    private TextView mTvPlayed;     //已播放时长
    private TextView mTvAll;        //PPT总时长
    private SeekBar mSbProgress;    //PPT播放进度

    private ImageView mIvControl;   //播放/暂停

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_details, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ToastUtil.makeToast("响应了");
            }
        });
        bar.setBackgroundAlpha(0);
    }

    @Override
    public void findViews() {
        mNivPlay = findView(R.id.meeting_ppt_play_niv);

        mTvPlayed = findView(R.id.meeting_ppt_played_time_tv);
        mTvAll = findView(R.id.meeting_ppt_all_time_tv);
        mSbProgress = findView(R.id.meeting_ppt_progress_sb);

        mIvControl = findView(R.id.meeting_ppt_control_iv);
    }

    @Override
    public void setViewsValue() {
        mNivPlay.placeHolder(R.mipmap.meeting_ic_details_paly_default).load();

        setOnClickListener(R.id.meeting_ppt_first_iv);
        setOnClickListener(R.id.meeting_ppt_last_iv);
        setOnClickListener(R.id.meeting_ppt_next_iv);
        setOnClickListener(R.id.meeting_ppt_single_details_iv);

        mSbProgress.setOnClickListener(this);
        mIvControl.setOnClickListener(this);
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_ppt_first_iv:         //回到第一页
                break;
            case R.id.meeting_ppt_last_iv:          //上一个
                break;
            case R.id.meeting_ppt_next_iv:          //下一个
                break;
            case R.id.meeting_ppt_single_details_iv://单个详情
                break;
            case R.id.meeting_ppt_progress_sb:      //PPT播放进度
                break;
            case R.id.meeting_ppt_control_iv:       //播放/暂停
            default:
                break;
        }

    }
}
