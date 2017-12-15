package jx.csp.presenter;

import android.content.Context;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import jx.csp.contact.LiveVideoContract;
import jx.csp.contact.LiveVideoContract.V;
import jx.csp.util.Util;
import lib.jx.contract.BasePresenterImpl;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.live.LiveListener;
import lib.live.LiveView;
import lib.live.push.PushManager;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */

public class LiveVideoPresenterImpl extends BasePresenterImpl<V> implements
        LiveVideoContract.P,
        OnCountDownListener {

    private final String TAG = "LiveVideoPresenterImpl";
    private final int KCountDownTime = 15;  // 开始倒计时的分钟数

    private boolean mUseFrontCamera = false;
    private boolean mMute = false;
    private boolean mShowCountDownRemainTv = false;

    private CountDown mCountDown;
    private long mStartTime;
    private long mStopTime;

    public LiveVideoPresenterImpl(V v) {
        super(v);

        mCountDown = new CountDown();
        mCountDown.setListener(this);
    }

    @Override
    public void initLive(Context context, LiveView liveView) {
        PushManager.getInst().init(context, liveView);
        PushManager.getInst().setPushListener(new MyPushListener());
    }

    @Override
    public void startCountDown(long startTime, long stopTime, long serverTime) {
        mStartTime = startTime;
        mStopTime = stopTime;
        mCountDown.start((mStopTime - serverTime) / TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public void startLive(String rtmpUrl) {
        PushManager.getInst().startLive(rtmpUrl);
        getView().startLiveState();
    }

    @Override
    public void stopLive() {
        PushManager.getInst().stopLive();
        getView().stopLiveState();
    }

    @Override
    public void switchCamera() {
        mUseFrontCamera = !mUseFrontCamera;
        PushManager.getInst().switchCamera();
    }

    @Override
    public void mute() {
        getView().setSilenceIvSelected(mMute);
        mMute = !mMute;
        PushManager.getInst().mute(mMute);
    }

    @Override
    public void onDestroy() {
        PushManager.getInst().finishLive();
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
    public void onCountDownErr() {}

    private class MyPushListener extends LiveListener {

        @Override
        public void onPushFail() {
            PushManager.getInst().stopLive();
            getView().liveFailState();
        }

        @Override
        public void onNetStatus(Bundle var1) {}
    }

}
