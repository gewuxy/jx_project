package jx.csp.ui.activity.liveroom;

import android.app.Service;
import android.support.annotation.NonNull;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.ui.activity.liveroom.LiveRoomContract.LiveRoomPresenter;
import jx.csp.ui.activity.liveroom.LiveRoomContract.LiveRoomView;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.ui.activity.base.BaseActivity;
import lib.zego.ZegoApiManager;

/**
 * 直播推流界面
 *
 * @author CaiXiang
 * @since 2017/9/20
 */

public class LiveRoomActivity extends BaseActivity implements LiveRoomView {

    private final int KPermissionCode = 10;
    private final int KSixty = 60;

    private TextureView mTextureView;
    private TextView mTvLiveTime;
    private TextView mTvRemainingTime;
    private ImageView mIvSilence;
    private TextView mTvOnlineNum;
    private ImageView mIvState;
    private TextView mTvState;
    private ImageView mIvLive;
    private TextView mTvNoCameraPermission;
    private TextView mTvStart;

    //测试暂时使用
    private String mRoomId = "789";
    private String mStreamId = "123";
    private String mTitle = "测试";
    private long mStartTime = System.currentTimeMillis() - 10 * 60 * 1000;
    private long mStopTime = System.currentTimeMillis() + 160 *60 * 1000;

    private LiveRoomPresenter mLiveRoomPresenter;
    private boolean mBeginCountDown = false;  // 是否开始倒计时,直播时间到了才开始
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private PhoneStateListener mPhoneStateListener = null;  // 电话状态监听

    @Override
    public void initData() {
        // 禁止手机锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ZegoApiManager.getInstance().init(this, "888", "敬信测试");
        mLiveRoomPresenter = new LiveRoomPresenterImpl(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_live_room;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mTextureView = findView(R.id.live_texture_view);
        mTvLiveTime = findView(R.id.live_tv_time);
        mTvRemainingTime = findView(R.id.live_tv_remaining_time);
        mIvSilence = findView(R.id.live_iv_silence);
        mTvOnlineNum = findView(R.id.live_tv_online_num);
        mIvState = findView(R.id.live_iv_state);
        mTvState = findView(R.id.live_tv_state);
        mIvLive = findView(R.id.live_iv_live);
        mTvNoCameraPermission = findView(R.id.live_tv_no_camera_permission);
        mTvStart = findView(R.id.live_tv_start);
    }

    @Override
    public void setViews() {
        //检查权限
        if (checkPermission(KPermissionCode, Permission.camera, Permission.micro_phone, Permission.phone)) {
            mLiveRoomPresenter.initLiveRoom(mRoomId);
            mLiveRoomPresenter.zegoCallback();
            initPhoneCallingListener();
            mIvLive.setClickable(true);
        } else {
            hideView(mTvStart);
            showView(mTvNoCameraPermission);
            mIvLive.setClickable(false);
        }

        setOnClickListener(R.id.live_iv_back);
        setOnClickListener(R.id.live_iv_silence);
        setOnClickListener(R.id.live_iv_switch_camera);
        setOnClickListener(R.id.live_tv_start);
        setOnClickListener(R.id.live_iv_live);
        //判断是否需要开始倒计时
        if (System.currentTimeMillis() >= mStartTime) {
            mBeginCountDown = true;
            mLiveRoomPresenter.startCountDown(mStartTime, mStopTime);
        }
    }

    public void initPhoneCallingListener() {
        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        YSLog.d(TAG, "call state idle " + state);
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        YSLog.d(TAG, "call state ringing " + state);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        YSLog.d(TAG, "call state off hook " + state);
                        if (mLiveState) {
                            mLiveRoomPresenter.stopLive();
                        }
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.live_iv_back: {
                finish();
            }
            break;
            case R.id.live_iv_silence: {
                mLiveRoomPresenter.useMic();
            }
            break;
            case R.id.live_iv_switch_camera: {
                mLiveRoomPresenter.switchCamera();
            }
            break;
            case R.id.live_tv_start: {
                if (Util.noNetwork()) {
                    return;
                }
                //判断直播时间是否到
                if (mBeginCountDown) {
                    mLiveRoomPresenter.startLive(mStreamId, mTitle);
                } else {
                    startCountDownAndLive();
                }
            }
            break;
            case R.id.live_iv_live: {
                if (Util.noNetwork()) {
                    return;
                }
                //判断直播时间是否到 mTvStart是否已经隐藏
                if (mBeginCountDown) {
                    if (mLiveState) {
                        mLiveRoomPresenter.stopLive();
                    } else {
                        mLiveRoomPresenter.startLive(mStreamId, mTitle);
                    }
                } else {
                    startCountDownAndLive();
                }
            }
            break;
        }
    }

    protected void startCountDownAndLive() {
        if (System.currentTimeMillis() >= mStartTime) {
            mBeginCountDown = true;
            mLiveRoomPresenter.startCountDown(mStartTime, mStopTime);
            mLiveRoomPresenter.startLive(mStreamId, mTitle);
        } else {
            showToast(R.string.live_time_no_arrived);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销电话监听
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mPhoneStateListener = null;
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mLiveRoomPresenter.onDestroy();
        ZegoApiManager.getInstance().releaseSDK();
    }

    @Override
    public void onPermissionResult(int code, int result) {
        if (code == KPermissionCode) {
            switch (result) {
                case PermissionResult.granted:
                    mLiveRoomPresenter.initLiveRoom(mRoomId);
                    mLiveRoomPresenter.zegoCallback();
                    initPhoneCallingListener();
                    hideView(mTvNoCameraPermission);
                    showView(mTvStart);
                    mIvLive.setClickable(true);
                    break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    hideView(mTvStart);
                    showView(mTvNoCameraPermission);
                    mIvLive.setClickable(false);
                }
                break;
            }
        }
    }

    @Override
    public TextureView getTextureView() {
        return mTextureView;
    }

    @Override
    public void setLiveTimeTv(String s) {
        mTvLiveTime.setText(s);
    }

    @Override
    public void setCountDownRemindTv(boolean show, int i) {
        if (show) {
            showView(mTvRemainingTime);
        }
        if (i >= KSixty) {
            mTvRemainingTime.setText(String.format(getString(R.string.live_stop_remind_minute), i/KSixty));
        } else {
            mTvRemainingTime.setText(String.format(getString(R.string.live_stop_remind_second), i));
        }
    }

    @Override
    public void setOnlineNumTv(int i) {
        mTvOnlineNum.setText(i + "");
    }

    @Override
    public void startLiveState() {
        mLiveState = true;
        hideView(mTvStart);
        mTvState.setText(R.string.live);
        mIvState.setSelected(mLiveState);
        mIvLive.setSelected(mLiveState);
    }

    @Override
    public void stopLiveState() {
        mLiveState = false;
        showView(mTvStart);
        mTvState.setText(R.string.no_start);
        mIvState.setSelected(mLiveState);
        mIvLive.setSelected(mLiveState);
    }

    @Override
    public void changeLiveIvRes() {
        mIvLive.setImageResource(R.drawable.selector_live_state_warm);
        mIvLive.setSelected(mLiveState);
    }

    @Override
    public void setSilenceIvSelected(boolean b) {
        mIvSilence.setSelected(b);
    }

    @Override
    public void onFinish() {
        finish();
    }
}
