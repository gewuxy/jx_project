package yy.doctor.ui.frag.meeting;

import android.support.annotation.NonNull;
import android.view.TextureView;

import inject.annotation.router.Route;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.ui.frag.base.BaseFrag;
import lib.zego.IZegoCallback;
import lib.zego.IZegoCallback.UserType;
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
    private ZegoCallbackImpl mZegoCallbackImpl;

    @Override
    public void initData() {
        mZegoCallbackImpl = new ZegoCallbackImpl();
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
        if (BuildConfig.TEST) {
            ZegoApiManager.getInstance().setTestEnv(true);
            roomId = "789";
        }
        ZegoApiManager.getInstance().loginRoom(roomId, UserType.audience, mZegoCallbackImpl);
        ZegoApiManager.getInstance().setZegoRoomCallback(mZegoCallbackImpl);
    }

    public void startAudio() {
        ZegoApiManager.getInstance().enableSpeaker(true);
    }

    public void stopAudio() {
        ZegoApiManager.getInstance().enableSpeaker(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ZegoApiManager.getInstance().logoutRoom();
    }

    private class ZegoCallbackImpl extends IZegoCallback {

        @Override
        public void onLoginCompletion(int i, String stream) {
            if (i == 0 && TextUtil.isNotEmpty(stream)) {
                YSLog.d(TAG, "loginRoom:success");
                ZegoApiManager.getInstance().startPlayingStream(stream, mViewLive);
            } else {
                // 登录失败(还是直播未开始)
                YSLog.d(TAG, "loginRoom:error ");
            }
        }

        @Override
        public void onUserUpdate(int number) {

        }

        @Override
        public void onStreamUpdated(int i, String stream) {
            if (i == IZegoCallback.Constants.KStreamAdd) {
                YSLog.d(TAG, "onStreamUpdated:play");
                if (TextUtil.isNotEmpty(stream)) {
                    ZegoApiManager.getInstance().startPlayingStream(stream, mViewLive);
                }
            } else if (i == IZegoCallback.Constants.KStreamDel) {
                YSLog.d(TAG, "onStreamUpdated:stop");
                if (TextUtil.isNotEmpty(stream)) {
                    ZegoApiManager.getInstance().stopPlayingStream(stream);
                }
            }

        }
    }
}
