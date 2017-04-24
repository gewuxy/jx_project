package yy.doctor.activity;

import android.support.annotation.NonNull;

import lib.ys.activity.ActivityEx;
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
    private NetworkImageView mPlay;
    //公众号图标
    private NetworkImageView mNumber;
    //嘉宾头像图片
    private NetworkImageView mGuest;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_details;
    }

    @Override
    public void initNavBar() {

    }

    @Override
    public void findViews() {
        mPlay = findView(R.id.meeting_details_play_niv);
        mNumber = findView(R.id.meeting_details_icon_number);
        mGuest = findView(R.id.meeting_ic_guest_default);
    }

    @Override
    public void setViewsValue() {
        mPlay.placeHolder(R.mipmap.meeting_ic_details_default).load();
        mNumber.placeHolder(R.mipmap.meeting_ic_number_default).load();
        mGuest.placeHolder(R.mipmap.meeting_ic_guest_default).load();
    }
}
