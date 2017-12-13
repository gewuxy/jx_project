package lib.live.push;

import android.content.Context;
import android.os.Bundle;

import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;

import lib.live.pull.PullListener;
import lib.live.ui.LiveView;

/**
 * 推流管理
 *
 * @auther : CaiXiang
 * @since : 2017/12/11
 */
public class PushManager {

    private static PushManager mInst;
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;

    public synchronized static PushManager getInst() {
        if (mInst == null) {
            mInst = new PushManager();
        }
        return mInst;
    }

    public void init(Context context, LiveView liveView) {
        TXLiveBase.setConsoleEnabled(true);
        TXLiveBase.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);
        mLivePusher = new TXLivePusher(context);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
        mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT);
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setRenderRotation(0);
        mLivePusher.startCameraPreview(liveView);
    }

    public void startLive(String rtmpUrl) {
        mLivePusher.startPusher(rtmpUrl);
    }

    public void stopLive() {
        mLivePusher.stopPusher();
    }

    public void setPushListener(PushListener l) {
        mLivePusher.setPushListener(new ITXLivePushListener() {

            @Override
            public void onPushEvent(int i, Bundle bundle) {
                l.onPushEvent(i, bundle);
            }

            @Override
            public void onNetStatus(Bundle bundle) {
                l.onNetStatus(bundle);
            }
        });
    }

    public void switchCamera() {
        mLivePusher.switchCamera();
    }

    /**
     * true:静音. false:不静音.
     * @param b
     */
    public void mute(boolean b) {
        mLivePusher.setMute(b);
    }

    public void finishLive() {
        mLivePusher.stopCameraPreview(true);
    }

}
