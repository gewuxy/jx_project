package lib.live;

import android.content.Context;

import lib.live.push.PushManager;

/**
 * @author CaiXiang
 * @since 2017/12/15
 */

public class LiveApi {

    private static LiveApi mLiveApi;

    public synchronized LiveApi inst() {
        if (mLiveApi == null) {
            mLiveApi = new LiveApi();
        }
        return mLiveApi;
    }

    public void initPushManager(Context context, LiveView liveView) {
        PushManager.inst().init(context, liveView);
    }

    public void startLive(String pushUrl) {
        PushManager.inst().startPush(pushUrl);
    }

    public void stopLive() {
        PushManager.inst().stopPush();
    }

    public void setPushListener(LiveListener l) {
        PushManager.inst().setPushListener(l);
    }

    public void switchCamera() {
        PushManager.inst().switchCamera();
    }

    public void mute(boolean b) {
        PushManager.inst().mute(b);
    }

    public void finishLive() {
        PushManager.inst().finishPush();
    }
}
