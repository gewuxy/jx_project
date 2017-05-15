package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import lib.network.error.NetError;
import lib.network.model.NetworkResponse;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.ToastUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Resp;
import yy.doctor.R;
import yy.doctor.model.exam.Course;
import yy.doctor.model.exam.Course.TCourse;
import yy.doctor.model.exam.Ppt;
import yy.doctor.model.exam.Ppt.TPpt;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 观看会议界面
 *
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class MeetingPPTActivity extends BaseActivity {

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
        mNivPlay = findView(R.id.meeting_ppt_iv_play_niv);

        mTvPlayed = findView(R.id.meeting_ppt_played_tv_time);
        mTvAll = findView(R.id.meeting_ppt_tv_all_time);
        mSbProgress = findView(R.id.meeting_ppt_sb_progress);

        mIvControl = findView(R.id.meeting_ppt_iv_control);
    }

    @Override
    public void setViews() {
        mNivPlay.placeHolder(R.mipmap.ic_default_meeting_single_detail).load();

        setOnClickListener(R.id.meeting_ppt_iv_first);
        setOnClickListener(R.id.meeting_ppt_iv_last);
        setOnClickListener(R.id.meeting_ppt_iv_next);
        setOnClickListener(R.id.meeting_ppt_iv_comment);

        mSbProgress.setOnClickListener(this);
        mIvControl.setOnClickListener(this);

        refresh(RefreshWay.embed);
        exeNetworkRequest(0, NetFactory.toPpt("17042512131640894904","7"));
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return JsonParser.ev(nr.getText(), Ppt.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Resp<Ppt> r = (Resp<Ppt>) result;
        if (r.isSucceed()) {
            Ppt data = r.getData();
            Course course = data.getEv(TPpt.course);
            LogMgr.d("标题",course.getString(TCourse.title));
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_ppt_iv_first:         //回到第一页
                break;
            case R.id.meeting_ppt_iv_last:          //上一个
                break;
            case R.id.meeting_ppt_iv_next:          //下一个
                break;
            case R.id.meeting_ppt_iv_comment://单个详情
                break;
            case R.id.meeting_ppt_sb_progress:      //PPT播放进度
                break;
            case R.id.meeting_ppt_iv_control:       //播放/暂停
            default:
                break;
        }

    }
}
