package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;

import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * 会议详情界面
 *
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */

public class MeetingDetailsActivity extends BaseActivity {

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
        mNivNumber = findView(R.id.meeting_niv_icon_number);
        mNivGuest = findView(R.id.meeting_niv_guest_portrait);
    }

    @Override
    public void setViews() {
        mNivPlay.placeHolder(R.mipmap.ic_default_meeting_content_detail).load();
        mNivNumber.placeHolder(R.mipmap.ic_default_meeting_number).load();
        mNivGuest.placeHolder(R.mipmap.ic_default_meeting_guest).load();
    }
}
