package jx.csp.presenter;

import android.view.Surface;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoConversationMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;
import com.zego.zegoliveroom.entity.ZegoUserState;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import jx.csp.BuildConfig;
import jx.csp.contact.LiveRoomContract;
import jx.csp.contact.LiveRoomContract.View;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import lib.zego.ZegoApiManager;


/**
 * @author CaiXiang
 * @since 2017/9/22
 */

public class LiveRoomPresenterImpl implements LiveRoomContract.Presenter, OnCountDownListener {

    private static final String TAG = "LiveRoomPresenterImpl";
    private final int KFifteen = 15;  // 开始倒计时的分钟数

    private View mView;
    private ZegoLiveRoom mZegoLiveRoom;

    private boolean mUseFrontCamera = false;
    private boolean mUseMic = true;
    private boolean mShowCountDownRemainTv = false;

    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;

    public LiveRoomPresenterImpl(View view) {
        mView = view;
    }

    @Override
    public void initLiveRoom(String roomId) {
        ZegoAvConfig avConfig = ZegoApiManager.getInstance().getZegoAvConfig();
        int w = avConfig.getVideoCaptureResolutionWidth();
        int h = avConfig.getVideoCaptureResolutionHeight();
        avConfig.setVideoEncodeResolution(h, w);
        avConfig.setVideoCaptureResolution(h, w);
        ZegoApiManager.getInstance().setZegoConfig(avConfig);

        mZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom();
        //测试
        mZegoLiveRoom.setTestEnv(BuildConfig.TEST);
        //回声消除
        mZegoLiveRoom.enableAEC(true);
        mZegoLiveRoom.enableMic(mUseMic);
        mZegoLiveRoom.enableCamera(true);
        //是否使用前置摄像头
        mZegoLiveRoom.setFrontCam(mUseFrontCamera);
        mZegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);
        mZegoLiveRoom.setAppOrientation(Surface.ROTATION_90);
        mZegoLiveRoom.setRoomConfig(true, true);
        mZegoLiveRoom.setPreviewView(mView.getTextureView());
        mZegoLiveRoom.startPreview();
        mZegoLiveRoom.loginRoom(roomId, ZegoConstants.RoomRole.Anchor, (i, zegoStreamInfos) -> {
            // i 状态码, 0:成功, 其它:失败
            YSLog.d(TAG, " onLoginCompletion i = " + i);
        });
    }

    @Override
    public void zegoCallback() {
        mZegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {

            @Override
            public void onPublishStateUpdate(int i, String s, HashMap<String, Object> hashMap) {
                // i   0:成功, 其它:失败
                YSLog.d(TAG, "推流状态更新" + i);
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {
                YSLog.d(TAG, "收到观众的连麦请求");
            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {
                YSLog.d(TAG, "推流质量更新");
            }

            @Override
            public AuxData onAuxCallback(int i) {
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int i, int i1) {
                YSLog.d(TAG, "采集视频的宽度和高度变化通知");
            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {
                YSLog.d(TAG, "混流配置更新");
            }
        });
        mZegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {

            @Override
            public void onKickOut(int i, String s) {
                YSLog.d(TAG, "因为登陆抢占原因等被挤出房间");
                // FIXME: 2017/10/24
            }

            @Override
            public void onDisconnect(int i, String s) {
                YSLog.d(TAG, "与 server 断开");
            }

            @Override
            public void onStreamUpdated(int i, ZegoStreamInfo[] zegoStreamInfos, String s) {
                YSLog.d(TAG, "房间流列表更新");
            }

            @Override
            public void onStreamExtraInfoUpdated(ZegoStreamInfo[] zegoStreamInfos, String s) {
                YSLog.d(TAG, "更新流的额外信息");
            }

            @Override
            public void onRecvCustomCommand(String s, String s1, String s2, String s3) {
                YSLog.d(TAG, "收到自定义消息");
            }
        });
        mZegoLiveRoom.setZegoIMCallback(new IZegoIMCallback() {

            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {
                //直播间的观众人数获取
                YSLog.d(TAG, "user state = " + zegoUserStates.length + "   i = " + i);
                mView.setOnlineNumTv(zegoUserStates.length);
            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {

            }

            @Override
            public void onRecvConversationMessage(String s, String s1, ZegoConversationMessage zegoConversationMessage) {

            }
        });
    }

    @Override
    public void startCountDown(long startTime, long stopTime) {
        mStartTime = startTime;
        mStopTime = stopTime;
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mCountDown.start((mStopTime - System.currentTimeMillis()) / TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public void startLive(String streamId, String title) {
        mZegoLiveRoom.startPublishing(streamId, title, ZegoConstants.PublishFlag.SingleAnchor);
        mView.startLiveState();
    }

    @Override
    public void stopLive() {
        mZegoLiveRoom.stopPublishing();
        mView.stopLiveState();
    }

    @Override
    public void switchCamera() {
        mUseFrontCamera = !mUseFrontCamera;
        mZegoLiveRoom.setFrontCam(mUseFrontCamera);
    }

    @Override
    public void useMic() {
        mView.setSilenceIvSelected(mUseMic);
        mUseMic = !mUseMic;
        mZegoLiveRoom.enableMic(mUseMic);
    }

    @Override
    public void onDestroy() {
        mZegoLiveRoom.stopPreview();
        mZegoLiveRoom.setZegoLivePublisherCallback(null);
        mZegoLiveRoom.setZegoRoomCallback(null);
        mZegoLiveRoom.setZegoIMCallback(null);
        mZegoLiveRoom.logoutRoom();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            mView.onFinish();
        }
        long time = (mStopTime - mStartTime) / 1000 - remainCount;
        mView.setLiveTimeTv(Util.getSpecialTimeFormat(time));
        if (remainCount <= TimeUnit.MINUTES.toSeconds(KFifteen)) {
            if (!mShowCountDownRemainTv) {
                mShowCountDownRemainTv = !mShowCountDownRemainTv;
                mView.changeLiveIvRes();
            }
            mView.setCountDownRemindTv(mShowCountDownRemainTv, (int) remainCount);
        }
    }

    @Override
    public void onCountDownErr() {
    }
}
