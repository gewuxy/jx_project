package lib.live.push;

import android.content.Context;
import android.os.Bundle;

import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;

import lib.live.ui.LiveView;

import static com.tencent.rtmp.TXLiveConstants.PUSH_ERR_AUDIO_ENCODE_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_ERR_NET_DISCONNECT;
import static com.tencent.rtmp.TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_ERR_VIDEO_ENCODE_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_DNS_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_NET_BUSY;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_READ_WRITE_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_SERVER_DISCONNECT;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_SEVER_CONN_FAIL;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_SHAKE_FAIL;

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
        mLivePushConfig.setANS(true); // 设置是否开启噪声抑制.
        mLivePushConfig.setTouchFocus(false); // 设置是否开启手动对焦.
        mLivePushConfig.setFrontCamera(false); // 设置开始的时候是否使用前置摄像头.
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setRenderRotation(0);
        mLivePusher.startCameraPreview(liveView);
    }

    public void startLive(String rtmpUrl) {
        if (!mLivePusher.isPushing()) {
            mLivePusher.startPusher(rtmpUrl);
        }
    }

    public void stopLive() {
        if (mLivePusher.isPushing()) {
            mLivePusher.stopPusher();
        }
    }

    public void setPushListener(PushListener l) {
        mLivePusher.setPushListener(new ITXLivePushListener() {

            @Override
            public void onPushEvent(int i, Bundle bundle) {
                switch (i) {
                    case PUSH_ERR_OPEN_CAMERA_FAIL: // 打开摄像头失败
                    case PUSH_ERR_OPEN_MIC_FAIL: // 打开麦克风失败
                    case PUSH_ERR_NET_DISCONNECT: // 网络断连
                    case PUSH_WARNING_NET_BUSY: //  网络状况不佳：上行带宽太小，上传数据受阻
                    case PUSH_WARNING_HW_ACCELERATION_FAIL:
                    case PUSH_WARNING_SHAKE_FAIL: // RTMP服务器握手失败
                    case PUSH_WARNING_SERVER_DISCONNECT: // RTMP服务器主动断开，请检查推流地址的合法性或防盗链有效期
                    case PUSH_WARNING_DNS_FAIL: // RTMP -DNS解析失败
                    case PUSH_WARNING_SEVER_CONN_FAIL: // RTMP服务器连接失败
                    case PUSH_WARNING_READ_WRITE_FAIL: // RTMP 读/写失败，将会断开连接
                    case PUSH_ERR_AUDIO_ENCODE_FAIL: // 音频编码失败
                    case PUSH_ERR_VIDEO_ENCODE_FAIL: // 视频编码失败
                        l.onPushFail();
                    break;
                }
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
