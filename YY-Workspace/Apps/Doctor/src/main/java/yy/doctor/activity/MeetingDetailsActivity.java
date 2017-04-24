package yy.doctor.activity;

import android.support.annotation.NonNull;

import lib.ys.activity.ActivityEx;
import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * 会议详情界面
 * <p>
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */

public class MeetingDetailsActivity extends ActivityEx {

    //播放图片
    private NetworkImageView mIvPlay;
    //公众号图标
    private NetworkImageView mIvNumber;
    //嘉宾头像图片
    private NetworkImageView mIvGuest;

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

    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_details_play_niv);
        mIvNumber = findView(R.id.meeting_details_icon_number);
        mIvGuest = findView(R.id.meeting_ic_guest_default);
    }

    @Override
    public void setViewsValue() {
        mIvPlay.placeHolder(R.mipmap.meeting_ic_details_default).load();
        mIvNumber.placeHolder(R.mipmap.meeting_ic_number_default).load();
        mIvGuest.placeHolder(R.mipmap.meeting_ic_guest_default).load();
    }
}
