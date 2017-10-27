package lib.zego;

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

/**
 * @author CaiXiang
 * @since 2017/9/20
 */
public class ZegoApiManager {
    private final String TAG = getClass().getSimpleName();

    // zego公司测试
    private static final long APP_ID = 1739272706L;
    private static final byte[] SIGN_KEY = new byte[]{(byte) 0x1e, (byte) 0xc3, (byte) 0xf8, (byte) 0x5c, (byte) 0xb2, (byte) 0xf2,
            (byte) 0x13, (byte) 0x70, (byte) 0x26, (byte) 0x4e, (byte) 0xb3, (byte) 0x71, (byte) 0xc8, (byte) 0xc6, (byte) 0x5c,
            (byte) 0xa3, (byte) 0x7f, (byte) 0xa3, (byte) 0x3b, (byte) 0x9d, (byte) 0xef, (byte) 0xef, (byte) 0x2a, (byte) 0x85,
            (byte) 0xe0, (byte) 0xc8, (byte) 0x99, (byte) 0xae, (byte) 0x82, (byte) 0xc0, (byte) 0xf6, (byte) 0xf8};

    // 公司
//    private static final long APP_ID = 3172899874L;
//    private static final byte[] SIGN_KEY = new byte[]{(byte) 0x70, (byte) 0x63, (byte) 0xcd, (byte) 0x6f, (byte) 0x52, (byte) 0xfd, (byte) 0x88,
//            (byte) 0xeb, (byte) 0x49, (byte) 0x52, (byte) 0xad, (byte) 0xd6, (byte) 0xa6, (byte) 0xdd, (byte) 0x57, (byte) 0xba, (byte) 0x1f,
//            (byte) 0x10, (byte) 0x42, (byte) 0x0b, (byte) 0x89, (byte) 0xd3, (byte) 0x9d, (byte) 0x7d, (byte) 0xa6, (byte) 0x04, (byte) 0x2f,
//            (byte) 0x7a, (byte) 0xda, (byte) 0xed, (byte) 0x75, (byte) 0x19};

    private static ZegoApiManager sInstance = null;
    private ZegoLiveRoom mZegoLiveRoom = null;
    private ZegoAvConfig mZegoAvConfig;

    private ZegoApiManager() {
        mZegoLiveRoom = new ZegoLiveRoom();
    }

    public synchronized static ZegoApiManager getInstance() {
        if (sInstance == null) {
            sInstance = new ZegoApiManager();
        }
        return sInstance;
    }

    public void init(Context context,String userId, String userName) {
        // 初始化用户信息 必须设置
        ZegoLiveRoom.setUser(userId, userName);
        // 初始化sdk 不传context会出现错误
        boolean ret = mZegoLiveRoom.initSDK(APP_ID, SIGN_KEY, context);
        if (ret) {
            // 初始化设置级别为"Generic"
            mZegoAvConfig = new ZegoAvConfig(Level.Generic);
            mZegoLiveRoom.setAVConfig(mZegoAvConfig);
        }
    }

    public ZegoLiveRoom getZegoLiveRoom() {
        return mZegoLiveRoom;
    }

    public void setZegoConfig(ZegoAvConfig config) {
        mZegoAvConfig = config;
        mZegoLiveRoom.setAVConfig(config);
    }

    public ZegoAvConfig getZegoAvConfig() {
        return mZegoAvConfig;
    }

    public void setTestEnv(boolean b) {
        mZegoLiveRoom.setTestEnv(b);
    }

    public void enableAEC(boolean b) {
        mZegoLiveRoom.enableAEC(b);
    }

    public void enableMic(boolean b) {
        mZegoLiveRoom.enableMic(b);
    }

    public void enableCamera(boolean b) {
        mZegoLiveRoom.enableCamera(b);
    }

    public void setFrontCam(boolean b) {
        mZegoLiveRoom.setFrontCam(b);
    }

    public void setPreviewViewMode(int model) {
        mZegoLiveRoom.setPreviewViewMode(model);
    }

    public void setAppOrientation(int orientation) {
        mZegoLiveRoom.setAppOrientation(orientation);
    }

    public void setPreviewView(Object view) {
        mZegoLiveRoom.setPreviewView(view);
    }

    public void startPreview() {
        mZegoLiveRoom.startPreview();
    }

    public void setRoomConfig(boolean audienceCreateRoom, boolean userStateUpdate) {
        mZegoLiveRoom.setRoomConfig(audienceCreateRoom, userStateUpdate);
    }

    public void startPublishing(String streamID, String title, int flag) {
        mZegoLiveRoom.startPublishing(streamID, title, flag);
    }

    public void stopPublishing() {
        mZegoLiveRoom.stopPublishing();
    }

    public void stopPreview() {
        mZegoLiveRoom.stopPreview();
    }

    public void enableSpeaker(boolean b) {
        mZegoLiveRoom.enableSpeaker(b);
    }

    public void startPlayingStream(String streamID, Object view) {
        mZegoLiveRoom.startPlayingStream(streamID, view);
    }

    public void logoutRoom() {
        mZegoLiveRoom.setZegoLivePublisherCallback(null);
        mZegoLiveRoom.setZegoRoomCallback(null);
        mZegoLiveRoom.setZegoIMCallback(null);
        mZegoLiveRoom.logoutRoom();
        ZegoLiveRoom.setTestEnv(false);
        mZegoLiveRoom.unInitSDK();
    }

    public void loginRoom(String roomID, int role, IZegoCallback callback) {
        mZegoLiveRoom.loginRoom(roomID, role, (i, zegoStreamInfos) -> {
            if (callback != null) {
                String stream;
                if (zegoStreamInfos != null && zegoStreamInfos.length > 0) {
                    stream = zegoStreamInfos[0].streamID;
                } else {
                    stream = "";
                }
                callback.onLoginCompletion(i, stream);
            }
        });
    }

    public void setZegoLivePublisherCallback(IZegoCallback callback) {
        mZegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {

            @Override
            public void onPublishStateUpdate(int i, String s, HashMap<String, Object> hashMap) {
                // i   0:成功, 其它:失败
                Log.d(TAG, "推流状态更新" + i);
                callback.onPublishStateUpdate(i);
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
    }

    public void setZegoRoomCallback(IZegoCallback callback) {
        mZegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {

            @Override
            public void onKickOut(int i, String s) {
                Log.d(TAG, "因为登陆抢占原因等被挤出房间");
                callback.onKickOut();
            }

            @Override
            public void onDisconnect(int i, String s) {
                Log.d(TAG, "与 server 断开");
            }

            @Override
            public void onStreamUpdated(int i, ZegoStreamInfo[] zegoStreamInfos, String s) {
                Log.d(TAG, "房间流列表更新");
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
    }

    public void setZegoIMCallback(IZegoCallback callback) {
        mZegoLiveRoom.setZegoIMCallback(new IZegoIMCallback() {

            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {
                //直播间的观众人数获取
                Log.d(TAG, "user state = " + zegoUserStates.length + "   i = " + i);
                if (zegoUserStates != null) {
                    callback.onUserUpdate(zegoUserStates.length);
                }
            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {

            }

            @Override
            public void onRecvConversationMessage(String s, String s1, ZegoConversationMessage zegoConversationMessage) {

            }
        });
    }

}
