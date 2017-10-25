package yy.doctor.ui.frag.meeting;

import android.support.annotation.NonNull;
import android.view.TextureView;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;

import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;
import lib.zego.ZegoApiManager;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;

/**
 * 直播部分
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class PPTLiveFrag extends BaseFrag {

    private TextureView mViewLive;
    private ZegoLiveRoom mLive;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_ppt_live;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // do nothing
    }

    @Override
    public void findViews() {
        mViewLive = findView(R.id.ppt_live_video);
    }

    @Override
    public void setViews() {

    }

    public void loginRoom(String roomId) {
        ZegoApiManager.getInstance().init(getContext(), Profile.inst().getString(TProfile.id), Profile.inst().getString(TProfile.linkman));
        mLive = ZegoApiManager.getInstance().getZegoLiveRoom();
        if (BuildConfig.TEST) {
            mLive.setTestEnv(true);
            roomId = "789";
        }
        mLive.loginRoom(roomId, ZegoConstants.RoomRole.Audience, (i, zegoStreamInfos) -> {
            if (i == 0 && zegoStreamInfos.length > 0) {
                mLive.startPlayingStream(zegoStreamInfos[0].streamID, mViewLive);
            } else {
                // 登录失败(还是直播未开始)
            }
        });
        mLive.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {

            @Override
            public void onPlayStateUpdate(int i, String s) {
                // 拉流状态更新
                mLive.startPlayingStream(s, mViewLive);
            }

            @Override
            public void onPlayQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {

            }

            @Override
            public void onInviteJoinLiveRequest(int i, String s, String s1, String s2) {

            }

            @Override
            public void onRecvEndJoinLiveCommand(String s, String s1, String s2) {

            }

            @Override
            public void onVideoSizeChangedTo(String s, int i, int i1) {

            }

        });
    }

    public void startAudio() {
        mLive.enableSpeaker(true);
    }

    public void stopAudio() {
        mLive.enableSpeaker(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLive.logoutRoom();
        ZegoApiManager.getInstance().releaseSDK();
    }
}
