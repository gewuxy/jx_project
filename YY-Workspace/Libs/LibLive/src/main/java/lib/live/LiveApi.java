package lib.live;

import android.content.Context;
import android.util.Log;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoAvConfig.Level;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoConversationMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;
import com.zego.zegoliveroom.entity.ZegoUserState;

import java.util.HashMap;

import lib.ys.YSLog;

/**
 * @author CaiXiang
 * @since 2017/9/20
 */
public class LiveApi {

    private final String TAG = getClass().getSimpleName();

    // zego公司测试
//    private static final long APP_ID = 1739272706L;
//    private static final byte[] SIGN_KEY = new byte[]{(byte) 0x1e, (byte) 0xc3, (byte) 0xf8, (byte) 0x5c, (byte) 0xb2, (byte) 0xf2,
//            (byte) 0x13, (byte) 0x70, (byte) 0x26, (byte) 0x4e, (byte) 0xb3, (byte) 0x71, (byte) 0xc8, (byte) 0xc6, (byte) 0x5c,
//            (byte) 0xa3, (byte) 0x7f, (byte) 0xa3, (byte) 0x3b, (byte) 0x9d, (byte) 0xef, (byte) 0xef, (byte) 0x2a, (byte) 0x85,
//            (byte) 0xe0, (byte) 0xc8, (byte) 0x99, (byte) 0xae, (byte) 0x82, (byte) 0xc0, (byte) 0xf6, (byte) 0xf8};

    // 公司
    private static final long APP_ID = 3172899874L;
    private static final byte[] SIGN_KEY = new byte[]{(byte) 0x70, (byte) 0x63, (byte) 0xcd, (byte) 0x6f, (byte) 0x52, (byte) 0xfd, (byte) 0x88,
            (byte) 0xeb, (byte) 0x49, (byte) 0x52, (byte) 0xad, (byte) 0xd6, (byte) 0xa6, (byte) 0xdd, (byte) 0x57, (byte) 0xba, (byte) 0x1f,
            (byte) 0x10, (byte) 0x42, (byte) 0x0b, (byte) 0x89, (byte) 0xd3, (byte) 0x9d, (byte) 0x7d, (byte) 0xa6, (byte) 0x04, (byte) 0x2f,
            (byte) 0x7a, (byte) 0xda, (byte) 0xed, (byte) 0x75, (byte) 0x19};

    private static LiveApi mInst = null;
    private ZegoLiveRoom mZegoLive = null;
    private ZegoAvConfig mZegoAvConfig;
    private boolean mUseHardwareEncode = false;
    private boolean mUseHardwareDecode = false;
    private boolean mUseRateControl = false;

    private LiveApi() {
        mZegoLive = new ZegoLiveRoom();
    }

    public synchronized static LiveApi getInst() {
        if (mInst == null) {
            mInst = new LiveApi();
        }
        return mInst;
    }

    public void init(Context context, String userId, String userName) {
        YSLog.d(TAG, "zego user id = " + userId + "  user name = " + userName);
        // 初始化用户信息 必须设置
        ZegoLiveRoom.setUser(userId, userName);
        // 初始化sdk 不传context会出现错误
        boolean ret = mZegoLive.initSDK(APP_ID, SIGN_KEY, context);
        if (ret) {
            // 初始化设置级别为"Generic"
            mZegoAvConfig = new ZegoAvConfig(Level.Generic);
            mZegoLive.setAVConfig(mZegoAvConfig);
            // 开发者根据需求定制
            // 硬件编码
            setUseHardwareEncode(mUseHardwareEncode);
            // 硬件解码
            setUseHardwareDecode(mUseHardwareDecode);
            // 码率控制
            setUseRateControl(mUseRateControl);
        }
    }

    public void setUseHardwareEncode(boolean useHardwareEncode) {
        if(useHardwareEncode){
            // 开硬编时, 关闭码率控制
            if(mUseRateControl){
                mUseRateControl = false;
                mZegoLive.enableRateControl(false);
            }
        }
        mUseHardwareEncode = useHardwareEncode;
        ZegoLiveRoom.requireHardwareEncoder(useHardwareEncode);
    }

    public void setUseHardwareDecode(boolean useHardwareDecode) {
        mUseHardwareDecode = useHardwareDecode;
        ZegoLiveRoom.requireHardwareDecoder(useHardwareDecode);
    }

    public void setUseRateControl(boolean useRateControl) {
        if(useRateControl){
            // 开码率控制时, 关硬编
            if(mUseHardwareEncode){
                mUseHardwareEncode = false;
                ZegoLiveRoom.requireHardwareEncoder(false);
            }
        }
        mUseRateControl = useRateControl;
        mZegoLive.enableRateControl(useRateControl);
    }

    public LiveApi toggleAVConfig() {
        int w = mZegoAvConfig.getVideoCaptureResolutionWidth();
        int h = mZegoAvConfig.getVideoCaptureResolutionHeight();
        mZegoAvConfig.setVideoEncodeResolution(h, w);
        mZegoAvConfig.setVideoCaptureResolution(h, w);
        mZegoLive.setAVConfig(mZegoAvConfig);
        return this;
    }

    public LiveApi setTest(boolean b) {
        mZegoLive.setTestEnv(b);
        return this;
    }

    public LiveApi enableAEC(boolean b) {
        mZegoLive.enableAEC(b);
        return this;
    }

    public LiveApi enableMic(boolean b) {
        mZegoLive.enableMic(b);
        return this;
    }

    public LiveApi enableCamera(boolean b) {
        mZegoLive.enableCamera(b);
        return this;
    }

    public LiveApi setFrontCam(boolean b) {
        mZegoLive.setFrontCam(b);
        return this;
    }

    public LiveApi setPreviewViewMode(int model) {
        mZegoLive.setPreviewViewMode(model);
        return this;
    }

    public LiveApi setAppOrientation(int orientation) {
        mZegoLive.setAppOrientation(orientation);
        return this;
    }

    public LiveApi setPreviewView(Object view) {
        mZegoLive.setPreviewView(view);
        mZegoLive.startPreview();
        return this;
    }

    public LiveApi setRoomConfig(boolean audienceCreateRoom, boolean userStateUpdate) {
        mZegoLive.setRoomConfig(audienceCreateRoom, userStateUpdate);
        return this;
    }

    public void startPublishing(String streamID, String title, int flag) {
        mZegoLive.startPublishing(streamID, title, flag);
    }

    public void stopPublishing() {
        mZegoLive.stopPublishing();
    }

    public void stopPreview() {
        mZegoLive.stopPreview();
    }

    public void audio(boolean b) {
        mZegoLive.enableSpeaker(b);
    }

    public boolean startPullStream(String streamID, Object view) {
        return mZegoLive.startPlayingStream(streamID, view);
    }

    public void stopPullStream(String streamID) {
        mZegoLive.stopPlayingStream(streamID);
    }

    public void logoutRoom() {
        mZegoLive.setZegoLivePublisherCallback(null);
        mZegoLive.setZegoRoomCallback(null);
        mZegoLive.setZegoIMCallback(null);
        mZegoLive.logoutRoom();
        mZegoLive.unInitSDK();
    }

    public LiveApi setCallback(String roomID, int role, ILiveCallback callback) {
        mZegoLive.loginRoom(roomID, role, (i, zegoStreamInfos) -> {
            YSLog.d(TAG, "登陆房间 0:成功, 其它:失败  i = " + i);
            if (callback != null) {
                callback.onLoginCompletion(i, getStream(zegoStreamInfos));
            }
        });
        mZegoLive.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {

            @Override
            public void onPublishStateUpdate(int i, String s, HashMap<String, Object> hashMap) {
                // i   0:成功, 其它:失败
                Log.d(TAG, "推流状态更新  0:成功, 其它:失败 i = " + i);
                YSLog.d(TAG, "login time = " + System.currentTimeMillis());
                if (callback != null) {
                    callback.onPublishStateUpdate(i);
                }
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {
                Log.d(TAG, "收到观众的连麦请求");
            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {
                Log.d(TAG, "推流质量更新");
            }

            @Override
            public AuxData onAuxCallback(int i) {
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int i, int i1) {
                Log.d(TAG, "采集视频的宽度和高度变化通知");
            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {
                Log.d(TAG, "混流配置更新");
            }
        });
        mZegoLive.setZegoRoomCallback(new IZegoRoomCallback() {

            @Override
            public void onKickOut(int i, String s) {
                Log.d(TAG, "因为登陆抢占原因等被挤出房间");
                if (callback != null) {
                    callback.onKickOut();
                }
            }

            @Override
            public void onDisconnect(int i, String s) {
                Log.d(TAG, "与 server 断开");
            }

            @Override
            public void onStreamUpdated(int i, ZegoStreamInfo[] zegoStreamInfos, String s) {
                Log.d(TAG, "房间流列表更新");
                if (callback != null) {
                    callback.onStreamUpdated(i, getStream(zegoStreamInfos));
                }
            }

            @Override
            public void onStreamExtraInfoUpdated(ZegoStreamInfo[] zegoStreamInfos, String s) {
                Log.d(TAG, "更新流的额外信息");
            }

            @Override
            public void onRecvCustomCommand(String s, String s1, String s2, String s3) {
                Log.d(TAG, "收到自定义消息");
            }
        });
        mZegoLive.setZegoIMCallback(new IZegoIMCallback() {

            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {
                //直播间的观众人数获取
                if (zegoUserStates != null && callback != null) {
                    callback.onUserUpdate(zegoUserStates.length);
                    Log.d(TAG, "user state = " + zegoUserStates.length + "   i = " + i);
                }
            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {

            }

            @Override
            public void onRecvConversationMessage(String s, String s1, ZegoConversationMessage zegoConversationMessage) {

            }
        });
        return this;
    }

    private String getStream(ZegoStreamInfo[] zegoStreamInfos) {
        if (zegoStreamInfos != null && zegoStreamInfos.length > 0) {
            return zegoStreamInfos[0].streamID;
        } else {
            return "";
        }
    }

}
