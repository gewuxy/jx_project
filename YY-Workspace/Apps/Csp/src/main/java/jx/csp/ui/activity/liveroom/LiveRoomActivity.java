package jx.csp.ui.activity.liveroom;

import android.app.Service;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.LiveRoomContract;
import jx.csp.dialog.BigButtonDialog;
import jx.csp.model.meeting.WebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.TWebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.WsOrderFrom;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.presenter.LiveRoomPresenterImpl;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.receiver.ConnectionReceiver;
import lib.ys.receiver.ConnectionReceiver.OnConnectListener;
import lib.ys.receiver.ConnectionReceiver.TConnType;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.LiveNotifier;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.notify.LiveNotifier.OnLiveNotify;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * 直播推流界面
 *
 * @author CaiXiang
 * @since 2017/9/20
 */
@Route
public class LiveRoomActivity extends BaseActivity implements OnLiveNotify, OnConnectListener {

    private final int KPermissionCode = 10;
    private final int KSixty = (int) TimeUnit.MINUTES.toSeconds(1);

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

    @Arg(opt = true)
    String mCourseId;  // 房间号
    @Arg(opt = true)
    String mStreamId;
    @Arg(opt = true)
    String mTitle;
    @Arg(opt = true)
    long mStartTime;
    @Arg(opt = true)
    long mStopTime;
    @Arg(opt = true)
    String mWsUrl;

    private WebSocketServRouter mWebSocketServRouter;
    // 实际结束时间比结束时间多15分钟
    private long mRealStopTime;

    private LiveRoomContract.Presenter mPresenter;
    private LiveRoomContract.View mView;

    private boolean mBeginCountDown = false;  // 是否开始倒计时,直播时间到了才开始
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mIsFlowInsufficient = false; // 流量是否不足 同时出现时间不足和流量不足的情况优先显示流量不足的提示；但在时间耗尽，流量还有的情况依旧结束直播
    private boolean mIsShowRemainingTimeTv = false; // 倒计时或者流量不足提示是否显示
    private PhoneStateListener mPhoneStateListener = null;  // 电话状态监听
    private ConnectionReceiver mConnectionReceiver;

    @Override
    public void initData(Bundle savedInstanceState) {
        LiveNotifier.inst().add(this);
        // 禁止手机锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mView = new View();
        mPresenter = new LiveRoomPresenterImpl(mView);
        mRealStopTime = mStopTime + TimeUnit.MINUTES.toMillis(15);
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
        // 检查权限
        if (checkPermission(KPermissionCode, Permission.camera, Permission.micro_phone, Permission.phone)) {
            havePermissionState();
        } else {
            noPermissionState();
        }

        setOnClickListener(R.id.live_iv_back);
        setOnClickListener(R.id.live_iv_silence);
        setOnClickListener(R.id.live_iv_switch_camera);
        setOnClickListener(R.id.live_tv_start);
        setOnClickListener(R.id.live_iv_live);
        //判断是否需要开始倒计时
        if (System.currentTimeMillis() >= mStartTime) {
            mBeginCountDown = true;
            mPresenter.startCountDown(mStartTime, mRealStopTime);
        }
        // 连接websocket
        mWebSocketServRouter = WebSocketServRouter.create(mWsUrl);
        mWebSocketServRouter.route(this);
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
                            mPresenter.stopLive();
                        }
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onClick(android.view.View v) {
        int id = v.getId();
        switch (id) {
            case R.id.live_iv_back: {
                if (mLiveState) {
                    mPresenter.stopLive();
                    mLiveState = false;
                }
                finish();
            }
            break;
            case R.id.live_iv_silence: {
                mPresenter.useMic();
            }
            break;
            case R.id.live_iv_switch_camera: {
                mPresenter.switchCamera();
            }
            break;
            case R.id.live_tv_start: {
                if (Util.noNetwork()) {
                    return;
                }
                //判断直播时间是否到
                if (mBeginCountDown) {
                    mPresenter.startLive(mStreamId, mTitle);
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
                        mPresenter.stopLive();
                        mLiveState = false;
                    } else {
                        mPresenter.startLive(mStreamId, mTitle);
                    }
                } else {
                    startCountDownAndLive();
                }
            }
            break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mConnectionReceiver = new ConnectionReceiver(this);
        mConnectionReceiver.setListener(this);
        mConnectionReceiver.register();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mConnectionReceiver.unRegister();
        if (mLiveState) {
            mPresenter.stopLive();
            mLiveState = false;
        }
        mView.stopLiveState();
    }

    protected void startCountDownAndLive() {
        if (System.currentTimeMillis() >= mStartTime) {
            mBeginCountDown = true;
            mPresenter.startCountDown(mStartTime, mRealStopTime);
            mPresenter.startLive(mStreamId, mTitle);
        } else {
            showToast(R.string.meeting_no_start_remain);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveNotifier.inst().remove(this);
        // 注销电话监听
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mPhoneStateListener = null;
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mPresenter.onDestroy();
        if (mWebSocketServRouter != null) {
            YSLog.d(TAG, "liveroomactivity WebSocketServRouter.stop");
            mWebSocketServRouter.stop(this);
        }
    }

    @Override
    public void onPermissionResult(int code, int result) {
        if (code == KPermissionCode) {
            switch (result) {
                case PermissionResult.granted:
                    havePermissionState();
                    break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    noPermissionState();
                }
                break;
            }
        }
    }

    @Override
    public void onLiveNotify(int type, Object data) {
        switch (type) {
            case LiveNotifyType.inquired: {
                switchLiveDevice();
            }
            break;
            case LiveNotifyType.online_num: {
                int num = (int) data;
                if (num >= 1) {
                    mTvOnlineNum.setText(String.valueOf(num - 1));
                }
            }
            break;
            case LiveNotifyType.flow_insufficient: {
                // 流量不足警告
                mIsFlowInsufficient = true;
                mIsShowRemainingTimeTv = true;
                showView(mTvRemainingTime);
                mTvRemainingTime.setText(R.string.live_stream_insufficient);
            }
            break;
            case LiveNotifyType.flow_run_out_of: {
                if (mLiveState) {
                    mPresenter.stopLive();
                }
                finish();
            }
            break;
        }
    }

    @Override
    public void onConnectChanged(TConnType type) {
        // 没有网络时的处理
        if (type == TConnType.disconnect) {
            showToast(R.string.network_disabled);
            if (mLiveState) {
                mPresenter.stopLive();
            }
            finish();
        }
    }

    private void havePermissionState() {
        mPresenter.initLiveRoom(mCourseId);
        initPhoneCallingListener();
        hideView(mTvNoCameraPermission);
        showView(mTvStart);
        mIvLive.setClickable(true);
    }

    private void noPermissionState() {
        hideView(mTvStart);
        showView(mTvNoCameraPermission);
        mIvLive.setClickable(false);
        mTvState.setText(R.string.live_fail);
    }

    private void switchLiveDevice() {
        YSLog.d(TAG, "直播间是否切换直播设备");
        BigButtonDialog dialog = new BigButtonDialog(this);
        dialog.setTextHint(ResLoader.getString(R.string.switch_live_record_device));
        CountDown countDown = new CountDown();
        countDown.start(5);
        dialog.addBlackButton(R.string.continue_host, view -> {
            sendWsMsg(WsOrderType.reject);
            countDown.stop();
        });
        TextView tv = dialog.addBlueButton(R.string.affirm_exit, view -> {
            // 如果在直播要先暂停，再退出页面
            if (mLiveState) {
                mPresenter.stopLive();
                mLiveState = false;
            }
            sendWsMsg(WsOrderType.accept);
            finish();
        });
        countDown.setListener(new OnCountDownListener() {

            @Override
            public void onCountDown(long remainCount) {
                if (remainCount == 0) {
                    if (mLiveState) {
                        mPresenter.stopLive();
                        mLiveState = false;
                    }
                    sendWsMsg(WsOrderType.accept);
                    dialog.dismiss();
                    finish();
                    return;
                }
                tv.setText(String.format(getString(R.string.affirm_exit), remainCount));
            }

            @Override
            public void onCountDownErr() {
            }
        });
        dialog.show();
    }

    private void sendWsMsg(@WsOrderType int type) {
        WebSocketMsg msg = new WebSocketMsg();
        msg.put(TWebSocketMsg.courseId, mCourseId);
        msg.put(TWebSocketMsg.order, type);
        msg.put(TWebSocketMsg.orderFrom, WsOrderFrom.app);
        LiveNotifier.inst().notify(LiveNotifyType.send_msg, msg.toJson());
    }

    private class View implements LiveRoomContract.View {

        @Override
        public TextureView getTextureView() {
            return mTextureView;
        }

        @Override
        public void setLiveTimeTv(String s) {
            mTvLiveTime.setText(s);
        }

        @Override
        public void setCountDownRemindTv(int i) {
            if (!mIsFlowInsufficient) {
                if (!mIsShowRemainingTimeTv) {
                    mIsShowRemainingTimeTv = true;
                    showView(mTvRemainingTime);
                }
                if (i >= KSixty) {
                    mTvRemainingTime.setText(String.format(getString(R.string.live_stop_remind_minute), i / KSixty));
                } else {
                    mTvRemainingTime.setText(String.format(getString(R.string.live_stop_remind_second), i));
                }
            }
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
        public void liveFailState() {
            mLiveState = false;
            showView(mTvStart);
            mTvState.setText(R.string.live_fail);
            mIvState.setSelected(mLiveState);
            mIvLive.setSelected(mLiveState);
            showToast(R.string.live_fail);
        }

        @Override
        public void changeLiveIvRes() {
            mIvLive.setImageResource(R.drawable.live_selector_video_state_warning);
            mIvLive.setSelected(mLiveState);
        }

        @Override
        public void setSilenceIvSelected(boolean b) {
            mIvSilence.setSelected(b);
        }

        @Override
        public void onFinish() {
            if (mLiveState) {
                mPresenter.stopLive();
            }
            finish();
        }

        @Override
        public void onStopRefresh() {

        }

        @Override
        public void setViewState(int state) {
        }
    }
}
