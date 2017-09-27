package jx.csp.ui.activity.liveroom;

import android.widget.Toast;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoAvConfig.Level;

import jx.csp.App;

/**
 * des: zego api管理器.
 */
public class ZegoApiManager {

    private static final long APP_ID =  1739272706L;
    private static final byte[] SIGN_KEY = new byte[] { (byte)0x1e, (byte)0xc3, (byte)0xf8, (byte)0x5c, (byte)0xb2, (byte)0xf2, (byte)0x13, (byte)0x70,
            (byte)0x26, (byte)0x4e, (byte)0xb3, (byte)0x71, (byte)0xc8, (byte)0xc6, (byte)0x5c, (byte)0xa3,
            (byte)0x7f, (byte)0xa3, (byte)0x3b, (byte)0x9d, (byte)0xef, (byte)0xef, (byte)0x2a, (byte)0x85,
            (byte)0xe0, (byte)0xc8, (byte)0x99, (byte)0xae, (byte)0x82, (byte)0xc0, (byte)0xf6, (byte)0xf8 };

    private static ZegoApiManager sInstance = null;
    private ZegoLiveRoom mZegoLiveRoom = null;
    private ZegoAvConfig mZegoAvConfig;

    private boolean mUseHardwareEncode = false;
    private boolean mUseHardwareDecode = false;
    private boolean mUseRateControl = false;

    private long mAppID = 0;
    private byte[] mSignKey = null;

    private ZegoApiManager() {
        mZegoLiveRoom = new ZegoLiveRoom();
    }

    public static ZegoApiManager getInstance() {
        if (sInstance == null) {
            synchronized (ZegoApiManager.class) {
                if (sInstance == null) {
                    sInstance = new ZegoApiManager();
                }
            }
        }
        return sInstance;
    }

    public void init(){
        mAppID = APP_ID;
        mSignKey = SIGN_KEY;
        // 初始化用户信息 必须设置
        String userID = "88888888";
        String userName = "敬信测试";
        ZegoLiveRoom.setUser(userID, userName);

        // 初始化sdk
        boolean ret = mZegoLiveRoom.initSDK(mAppID, mSignKey, App.getContext());
        if(!ret){
            // sdk初始化失败
            Toast.makeText(App.getContext(), "Zego SDK初始化失败!", Toast.LENGTH_LONG).show();
        } else {
            // 初始化设置级别为"High"
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


    public ZegoAvConfig getZegoAvConfig(){
        return  mZegoAvConfig;
    }

    public void setUseHardwareEncode(boolean useHardwareEncode) {
        if(useHardwareEncode){
            // 开硬编时, 关闭码率控制
            if(mUseRateControl){
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
        if(useRateControl){
            // 开码率控制时, 关硬编
            if(mUseHardwareEncode){
                mUseHardwareEncode = false;
                ZegoLiveRoom.requireHardwareEncoder(false);
            }
        }
        mUseRateControl = useRateControl;
        mZegoLiveRoom.enableRateControl(useRateControl);
    }

}
