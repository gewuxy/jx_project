package lib.zego;

import android.content.Context;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoAvConfig.Level;

/**
 * @author CaiXiang
 * @since 2017/9/20
 */
public class ZegoApiManager {

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

    private boolean mUseHardwareEncode = false;
    private boolean mUseHardwareDecode = false;
    private boolean mUseRateControl = false;

    private ZegoApiManager() {
        mZegoLiveRoom = new ZegoLiveRoom();
    }

    public synchronized static ZegoApiManager getInstance() {
        if (sInstance == null) {
            sInstance = new ZegoApiManager();
        }
        return sInstance;
    }

    public void init(Context context, String userId, String userName) {
        // 初始化用户信息 必须设置
        ZegoLiveRoom.setUser(userId, userName);
        // 初始化sdk
        boolean ret = mZegoLiveRoom.initSDK(APP_ID, SIGN_KEY, context);
        if (ret) {
            // 初始化设置级别为"Generic"
            mZegoAvConfig = new ZegoAvConfig(Level.Generic);
            mZegoLiveRoom.setAVConfig(mZegoAvConfig);
            // 开发者根据需求定制
            // 硬件编码
            setUseHardwareEncode(mUseHardwareEncode);
            // 硬件解码
            setUseHardwareDecode(mUseHardwareDecode);
            // 码率控制
            setUseRateControl(mUseRateControl);
        }
    }

    public void releaseSDK() {
        // 清空设置
        ZegoLiveRoom.setTestEnv(false);
        mZegoLiveRoom.unInitSDK();
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

    public void setUseHardwareEncode(boolean useHardwareEncode) {
        if (useHardwareEncode) {
            // 开硬编时, 关闭码率控制
            if (mUseRateControl) {
                mUseRateControl = false;
                mZegoLiveRoom.enableRateControl(false);
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
        if (useRateControl) {
            // 开码率控制时, 关硬编
            if (mUseHardwareEncode) {
                mUseHardwareEncode = false;
                ZegoLiveRoom.requireHardwareEncoder(false);
            }
        }
        mUseRateControl = useRateControl;
        mZegoLiveRoom.enableRateControl(useRateControl);
    }

}
