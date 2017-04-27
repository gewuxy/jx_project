package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;

import lib.ys.activity.ActivityEx;
import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * 会议详情界面
 *
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */

public class MeetingDetailsActivity extends ActivityEx {

    //播放图片
    private NetworkImageView mNivPlay;
    //公众号图标
    private NetworkImageView mNivNumber;
    //嘉宾头像图片
    private NetworkImageView mNivGuest;

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
        mNivPlay = findView(R.id.meeting_details_play_niv);
        mNivNumber = findView(R.id.meeting_details_icon_number);
        mNivGuest = findView(R.id.meeting_ic_guest_default);
    }

    @Override
    public void setViews() {
        mNivPlay.placeHolder(R.mipmap.meeting_ic_details_default).load();
        mNivNumber.placeHolder(R.mipmap.meeting_ic_number_default).load();
        mNivGuest.placeHolder(R.mipmap.meeting_ic_guest_default).load();
    }
}
