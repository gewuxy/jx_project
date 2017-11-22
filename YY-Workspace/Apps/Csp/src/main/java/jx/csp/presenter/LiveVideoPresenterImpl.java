package jx.csp.presenter;

import android.view.Surface;
import android.view.TextureView;

import java.util.concurrent.TimeUnit;

import jx.csp.App;
import jx.csp.BuildConfig;
import jx.csp.contact.LiveVideoContract;
import jx.csp.contact.LiveVideoContract.V;
import jx.csp.model.Profile;
import jx.csp.util.Util;
import lib.live.ILiveCallback;
import lib.live.ILiveCallback.UserType;
import lib.live.LiveApi;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */

public class LiveVideoPresenterImpl extends BasePresenterImpl<V> implements
        LiveVideoContract.P,
        OnCountDownListener {

    private static final String TAG = "LiveVideoPresenterImpl";
    private final int KCountDownTime = 15;  // 开始倒计时的分钟数

    private boolean mUseFrontCamera = false;
    private boolean mUseMic = true;
    private boolean mShowCountDownRemainTv = false;

    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;

    private LiveCallbackImpl mZegoCallbackImpl;

    public LiveVideoPresenterImpl(V v) {
        super(v);

        LiveApi.getInst().init(App.getContext(), Profile.inst().getUserId(), Profile.inst().getUserName());
        mZegoCallbackImpl = new LiveCallbackImpl();
        mCountDown = new CountDown();
        mCountDown.setListener(this);
    }

    @Override
    public void initLiveRoom(String roomId, TextureView textureView) {
        LiveApi.getInst()
                .setTest(BuildConfig.DEBUG_NETWORK)  // 测试
                .toggleAVConfig()
                .enableAEC(true)  // 回声消除
                .enableMic(mUseMic)
                .enableCamera(true)
                .setFrontCam(mUseFrontCamera)  // 是否使用前置摄像头
                .setPreviewViewMode(ILiveCallback.Constants.KAspectFill)
                .setAppOrientation(Surface.ROTATION_90)
                .setRoomConfig(true, true)
                .setPreviewView(textureView)
                .setCallback(roomId, UserType.anchor, mZegoCallbackImpl);
    }

    @Override
    public void startCountDown(long startTime, long stopTime) {
        mStartTime = startTime;
        mStopTime = stopTime;
        mCountDown.start((mStopTime - System.currentTimeMillis()) / TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public void startLive(String streamId, String title) {
        LiveApi.getInst().startPublishing(streamId, title, ILiveCallback.Constants.KSingleAnchor);
        getView().startLiveState();
    }

    @Override
    public void stopLive() {
        LiveApi.getInst().stopPublishing();
        getView().stopLiveState();
    }

    @Override
    public void switchCamera() {
        mUseFrontCamera = !mUseFrontCamera;
        LiveApi.getInst().setFrontCam(mUseFrontCamera);
    }

    @Override
    public void useMic() {
        getView().setSilenceIvSelected(mUseMic);
        mUseMic = !mUseMic;
        LiveApi.getInst().enableMic(mUseMic);
    }

    @Override
    public void onDestroy() {
        LiveApi.getInst().stopPreview();
        LiveApi.getInst().logoutRoom();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            getView().finishLive();
        }
        long time = (mStopTime - mStartTime) / 1000 - remainCount;
        getView().setLiveTime(Util.getSpecialTimeFormat(time, "'", "''"));
        if (remainCount <= TimeUnit.MINUTES.toSeconds(KCountDownTime)) {
            if (!mShowCountDownRemainTv) {
                mShowCountDownRemainTv = true;
                getView().changeLiveIvRes();
            }
            getView().setCountDownRemind((int) remainCount);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    private class LiveCallbackImpl extends ILiveCallback {

        private final int KSuccess = 0; // 成功

        @Override
        public void onLoginCompletion(int i, String stream) {
            // i  登陆房间是否成功  0:成功, 其它:失败
            // if (i != KSuccess) {}
        }

        @Override
        public void onPublishStateUpdate(int i) {
            // i  推流是否成功  0:成功, 其它:失败
            if (i != KSuccess) {
                getView().liveFailState();
            }
        }

        @Override
        public void onKickOut() {
            getView().finishLive();
        }

        @Override
        public void onUserUpdate(int number) {}
    }
}
