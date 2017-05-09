package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.network.error.NetError;
import lib.network.model.NetworkResponse;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Response;
import yy.doctor.BuildConfig;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.model.meet.DetailInfo;
import yy.doctor.model.meet.DetailInfo.TDetailInfo;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 会议详情界面
 *
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */

public class MeetingDetailsActivity extends BaseActivity {

    private String mMeetId;
    private TextView mTvAward;

    public static void nav(Context context, String meetId) {
        Intent i = new Intent(context, SignActivity.class);
        i.putExtra(Extra.KData, meetId);
        LaunchUtil.startActivity(context, i);
    }

    //播放图片
    private NetworkImageView mIvPlay;
    //公众号图标
    private NetworkImageView mIvNumber;
    //嘉宾头像图片
    private NetworkImageView mIvGuest;

    private LinearLayout mLlExam;
    private LinearLayout mLlQuestion;
    private LinearLayout mLlVideo;
    private LinearLayout mLlSign;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_details;
    }

    @Override
    public void initData() {
        Intent i = getIntent();
        if (i != null) {
            mMeetId = i.getStringExtra(Extra.KData);
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        //TODO:右边图标的事件
        Util.addBackIcon(bar, "会议详情", this);
        bar.addViewRight(R.mipmap.nar_bar_ic_collection, v -> showToast("收藏"));
        bar.addViewRight(R.mipmap.nav_bar_ic_share, v -> {
            new ShareDialog(MeetingDetailsActivity.this).show();
            LogMgr.d(TAG, "分享");
        });
    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_details_play_niv);
        mIvNumber = findView(R.id.meeting_niv_icon_number);
        mIvGuest = findView(R.id.meeting_niv_guest_portrait);
        mTvAward = findView(R.id.meeting_details_tv_award);

        mLlExam = findView(R.id.meeting_details_exam);
        mLlQuestion = findView(R.id.meeting_details_questionnaire);
        mLlVideo = findView(R.id.meeting_details_video);
        mLlSign = findView(R.id.meeting_details_sign);

    }

    @Override
    public void setViews() {
        mIvPlay.placeHolder(R.mipmap.ic_default_meeting_content_detail).load();
        mIvNumber.placeHolder(R.mipmap.ic_default_meeting_number).load();
        mIvGuest.placeHolder(R.mipmap.ic_default_meeting_guest).load();

        setOnClickListener(R.id.meeting_details_exam);
        setOnClickListener(R.id.meeting_details_questionnaire);
        setOnClickListener(R.id.meeting_details_video_seeing);
        setOnClickListener(R.id.meeting_details_video);
        setOnClickListener(R.id.meeting_details_sign);

        if (BuildConfig.TEST) {
            mMeetId = "17042512131640894904";
        }

        refresh(RefreshWay.embed);
        exeNetworkRequest(0, NetFactory.meetInfo(mMeetId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return JsonParser.ev(nr.getText(), DetailInfo.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Response<DetailInfo> r = (Response<DetailInfo>) result;
        if (r.isSucceed()) {
            runOnUIThread(() -> refreshViews(r.getData()));
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    private void refreshViews(DetailInfo info) {
        mTvAward.setText("本次会议奖励象数:" + info.getString(TDetailInfo.xsCredits) + ",还有260人能够获得奖励.");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            //区分问卷还是考试
            case R.id.meeting_details_exam:
            case R.id.meeting_details_questionnaire:
                startActivity(ExamIntroActivity.class);
                break;
            case R.id.meeting_details_video_seeing:
            case R.id.meeting_details_video:
                startActivity(MeetingPPTActivity.class);
                break;
            case R.id.meeting_details_sign:
                startActivity(SignActivity.class);
                break;
            default:
                break;
        }

    }
}
