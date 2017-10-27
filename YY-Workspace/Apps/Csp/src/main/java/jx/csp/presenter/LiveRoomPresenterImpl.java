package jx.csp.presenter;

import android.view.Surface;

import java.util.concurrent.TimeUnit;

import jx.csp.App;
import jx.csp.BuildConfig;
import jx.csp.contact.LiveRoomContract;
import jx.csp.contact.LiveRoomContract.View;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import lib.zego.IZegoCallback;
import lib.zego.IZegoCallback.UserType;
import lib.zego.ZegoApiManager;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */

public class LiveRoomPresenterImpl implements LiveRoomContract.Presenter, OnCountDownListener {

    private static final String TAG = "LiveRoomPresenterImpl";
    private final int KFifteen = 15;  // 开始倒计时的分钟数

    private View mView;

    private boolean mUseFrontCamera = false;
    private boolean mUseMic = true;
    private boolean mShowCountDownRemainTv = false;

    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;

    private ZegoCallbackImpl mZegoCallbackImpl;

    public LiveRoomPresenterImpl(View view) {
        ZegoApiManager.getInstance().init(App.getContext(),"888", "敬信测试");
        mView = view;
        mZegoCallbackImpl = new ZegoCallbackImpl();
    }

    @Override
    public void initLiveRoom(String roomId) {
        ZegoApiManager.getInstance().toggleAVConfig();

        //测试
        ZegoApiManager.getInstance().setTestEnv(BuildConfig.TEST);
        //回声消除
        ZegoApiManager.getInstance().enableAEC(true);
        ZegoApiManager.getInstance().enableMic(mUseMic);
        ZegoApiManager.getInstance().enableCamera(true);
        //是否使用前置摄像头
        ZegoApiManager.getInstance().setFrontCam(mUseFrontCamera);
        ZegoApiManager.getInstance().setPreviewViewMode(IZegoCallback.Constants.KAspectFill);
        ZegoApiManager.getInstance().setAppOrientation(Surface.ROTATION_90);
        ZegoApiManager.getInstance().setRoomConfig(true, true);
        ZegoApiManager.getInstance().setPreviewView(mView.getTextureView());
        ZegoApiManager.getInstance().startPreview();
        ZegoApiManager.getInstance().loginRoom(roomId, UserType.anchor, mZegoCallbackImpl);
    }

    @Override
    public void zegoCallback() {
        ZegoApiManager.getInstance().setZegoLivePublisherCallback(mZegoCallbackImpl);
        ZegoApiManager.getInstance().setZegoRoomCallback(mZegoCallbackImpl);
        ZegoApiManager.getInstance().setZegoIMCallback(mZegoCallbackImpl);
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
        ZegoApiManager.getInstance().startPublishing(streamId, title, IZegoCallback.Constants.KSingleAnchor);
        mView.startLiveState();
    }

    @Override
    public void stopLive() {
        ZegoApiManager.getInstance().stopPublishing();
        mView.stopLiveState();
    }

    @Override
    public void switchCamera() {
        mUseFrontCamera = !mUseFrontCamera;
        ZegoApiManager.getInstance().setFrontCam(mUseFrontCamera);
    }

    @Override
    public void useMic() {
        mView.setSilenceIvSelected(mUseMic);
        mUseMic = !mUseMic;
        ZegoApiManager.getInstance().enableMic(mUseMic);
    }

    @Override
    public void onDestroy() {
        ZegoApiManager.getInstance().stopPreview();
        ZegoApiManager.getInstance().logoutRoom();
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

    private class ZegoCallbackImpl extends IZegoCallback {

        @Override
        public void onLoginCompletion(int i, String stream) {
            // i   0:成功, 其它:失败
            YSLog.d(TAG, "i" + i);
        }

        @Override
        public void onKickOut() {
            // FIXME: 2017/10/26 因为登陆抢占原因等被挤出房间
        }

        @Override
        public void onUserUpdate(int number) {
            mView.setOnlineNumTv(number);
        }
    }
}
