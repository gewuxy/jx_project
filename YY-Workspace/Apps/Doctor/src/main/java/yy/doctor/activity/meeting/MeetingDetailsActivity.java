package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import lib.ys.LogMgr;
import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.util.Util;

/**
 * 会议详情界面
 * <p>
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */

public class MeetingDetailsActivity extends BaseActivity {

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

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_details;
    }

    @Override
    public void initNavBar(NavBar bar) {
        //TODO:右边图标的事件
        Util.addBackIcon(bar, "会议详情", this);
        bar.addViewRight(R.mipmap.nar_bar_ic_collection, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("收藏");
            }
        });
        bar.addViewRight(R.mipmap.nav_bar_ic_share, new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareDialog(MeetingDetailsActivity.this).show();
                LogMgr.d(TAG,"分享");
            }
        });
    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_details_play_niv);
        mIvNumber = findView(R.id.meeting_niv_icon_number);
        mIvGuest = findView(R.id.meeting_niv_guest_portrait);

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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.meeting_details_exam:
                startActivity(ExamIntroActivity.class);
                break;
            case R.id.meeting_details_questionnaire:
                break;
            case R.id.meeting_details_video_seeing:
            case R.id.meeting_details_video:
                startActivity(MeetingDetailsActivity.class);
                break;
            case R.id.meeting_details_sign:
                startActivity(SignActivity.class);
                break;
            default:
                break;
        }

    }
}
