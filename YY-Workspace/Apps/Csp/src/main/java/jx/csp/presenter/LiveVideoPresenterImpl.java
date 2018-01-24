package jx.csp.presenter;

import android.content.Context;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import jx.csp.contact.LiveVideoContract;
import jx.csp.contact.LiveVideoContract.V;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.jx.contract.BasePresenterImpl;
import lib.jx.notify.Notifier;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.live.LiveListener;
import lib.live.LiveView;
import lib.live.push.PushManager;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */

public class LiveVideoPresenterImpl extends BasePresenterImpl<V> implements
        LiveVideoContract.P,
        OnCountDownListener {

    private final String TAG = "LiveVideoPresenterImpl";
    private final int KCountDownTime = 15;  // 开始倒计时的分钟数
    private final int KStartLiveReqId = 1;

    private boolean mUseFrontCamera = false;
    private boolean mMute = false;
    private boolean mShowCountDownRemainTv = false;
    private boolean mLiveState = false; // 直播状态

    private CountDown mCountDown;

    public LiveVideoPresenterImpl(V v) {
        super(v);

        mCountDown = new CountDown();
        mCountDown.setListener(this);
    }

    @Override
    public void initLive(Context context, LiveView liveView) {
        PushManager.inst().init(context, liveView);
        PushManager.inst().setPushListener(new MyPushListener());
    }

    @Override
    public void startCountDown(long time) {
        mCountDown.start(time / TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public void startLive(String courseId, String rtmpUrl, boolean mute, int liveState) {
        if (liveState == LiveState.un_start) {
            exeNetworkReq(KStartLiveReqId, MeetingAPI.liveVideoStart(courseId).build());
            Notifier.inst().notify(Notifier.NotifyType.start_live, courseId);
        }
        mLiveState = true;
        PushManager.inst().startPush(rtmpUrl);
        mMute = mute;
        PushManager.inst().mute(mMute);
        getView().startLiveState();
    }

    @Override
    public void stopLive() {
        mLiveState = false;
        PushManager.inst().stopPush();
        getView().stopLiveState();
    }

    @Override
    public void switchCamera() {
        mUseFrontCamera = !mUseFrontCamera;
        PushManager.inst().switchCamera();
    }

    @Override
    public void mute() {
        mMute = !mMute;
        getView().setSilenceIvSelected(mMute);
        PushManager.inst().mute(mMute);
    }

    @Override
    public void onDestroy() {
        PushManager.inst().finishPush();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            getView().finishLive();
        }
        if (remainCount <= TimeUnit.MINUTES.toSeconds(KCountDownTime)) {
            if (!mShowCountDownRemainTv) {
                mShowCountDownRemainTv = true;
                getView().changeLiveIvRes();
            }
            getView().setCountDownRemind((int) remainCount);
        }
        if (mLiveState) {
            getView().setLiveTime();
        }
    }

    @Override
    public void onCountDownErr() {}

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KStartLiveReqId) {
            if (r.isSucceed()) {
                YSLog.d(TAG, "视频直播开始成功");
            } else {
                YSLog.d(TAG, "视频直播开始失败， 重试");
                retryNetworkRequest(id);
            }
        }
    }

    private class MyPushListener extends LiveListener {

        @Override
        public void onPushFail() {
            PushManager.inst().stopPush();
            mLiveState = false;
            getView().liveFailState();
        }

        @Override
        public void onNetStatus(Bundle var1) {}
    }

}
