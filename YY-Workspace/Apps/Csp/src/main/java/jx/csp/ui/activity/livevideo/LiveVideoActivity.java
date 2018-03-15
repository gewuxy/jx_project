package jx.csp.ui.activity.livevideo;

import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.LiveVideoContract;
import jx.csp.dialog.BtnVerticalDialog;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.WebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.TWebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.WsOrderFrom;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.presenter.LiveVideoPresenterImpl;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.util.Util;
import lib.jx.notify.LiveNotifier;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.notify.LiveNotifier.OnLiveNotify;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.live.LiveView;
import lib.ys.YSLog;
import lib.ys.receiver.ConnectionReceiver;
import lib.ys.receiver.ConnectionReceiver.OnConnectListener;
import lib.ys.receiver.ConnectionReceiver.TConnType;
import lib.ys.receiver.PhoneReceiver;
import lib.ys.receiver.PhoneReceiver.PhoneListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;

/**
 * 直播推流界面
 *
 * @author CaiXiang
 * @since 2017/9/20
 */
@Route
public class LiveVideoActivity extends BaseActivity implements OnLiveNotify, OnConnectListener, PhoneListener {

    private final int KPermissionCode = 10;

    private LiveView mTextureView;
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
    String mCourseId;
    @Arg(opt = true)
    long mStartTime;
    @Arg(opt = true)
    long mServerTime;
    @Arg(opt = true)
    String mWsUrl;
    @Arg(opt = true)
    String mPushUrl;
    @Arg(opt = true)
    int mLiveVideoState;

    private long mEndTime;
    private LiveVideoContract.P mP;
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mIsFlowInsufficient = false; // 流量是否不足 同时出现时间不足和流量不足的情况优先显示流量不足的提示；但在时间耗尽，流量还有的情况依旧结束直播
    private boolean mIsShowRemainingTimeTv = false; // 倒计时或者流量不足提示是否显示
    protected PhoneReceiver mPhoneReceiver = null;  // 电话状态监听
    private ConnectionReceiver mConnectionReceiver;
    private boolean mSendAcceptOrReject = false;  // 是否已经发送过同意或拒绝被踢指令
    private int mLiveTime = 0;  // 直播的时间

    @Override
    public void initData() {
        // 禁止手机锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mP = new LiveVideoPresenterImpl(new View());

        if (mLiveVideoState == LiveState.un_start) {
            mStartTime = mServerTime;
        } else {
            mLiveTime = (int) ((mServerTime - mStartTime) / 1000);
        }
        if (mStartTime > mServerTime) {
            mStartTime = mServerTime;
        }
        mEndTime = mStartTime + TimeUnit.DAYS.toMillis(1);
        YSLog.d(TAG, "mServerTime = " + TimeFormatter.milli(mServerTime, TimeFormat.from_y_24));
        YSLog.d(TAG, "mStartTime = " + TimeFormatter.milli(mStartTime, TimeFormat.from_y_24));
        YSLog.d(TAG, "mEndTime = " + TimeFormatter.milli(mEndTime, TimeFormat.from_y_24));
        mConnectionReceiver = new ConnectionReceiver(this);
        mConnectionReceiver.setListener(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_live_video;
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

        mTvLiveTime.setText(Util.getSpecialTimeFormat(mLiveTime, "'", "''"));
        long countDownTime = mEndTime - mServerTime;
        YSLog.d(TAG, "mEndTime - mServerTime = " + countDownTime);
        if (countDownTime >= 0) {
            mP.startCountDown(countDownTime);
        }

        // 连接websocket
        if (mWsUrl != null) {
            WebSocketServRouter.create(mWsUrl).route(this);
        }

        LiveNotifier.inst().add(this);
    }

    @Override
    public void onClick(android.view.View v) {
        int id = v.getId();
        switch (id) {
            case R.id.live_iv_back: {
                if (mLiveState) {
                    mP.stopLive();
                }
                finish();
            }
            break;
            case R.id.live_iv_silence: {
                mP.mute();
            }
            break;
            case R.id.live_iv_switch_camera: {
                mP.switchCamera();
            }
            break;
            case R.id.live_tv_start: {
                if (Util.noNetwork()) {
                    return;
                }
                mP.startLive(mCourseId, mPushUrl, mIvSilence.isSelected(), mLiveVideoState);
            }
            break;
            case R.id.live_iv_live: {
                if (Util.noNetwork()) {
                    return;
                }
                if (mLiveState) {
                    mP.stopLive();
                } else {
                    mP.startLive(mCourseId, mPushUrl, mIvSilence.isSelected(), mLiveVideoState);
                }
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mConnectionReceiver.register();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mConnectionReceiver.unRegister();
        if (mLiveState) {
            mP.stopLive();
            mLiveState = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveNotifier.inst().remove(this);

        // 注销电话监听
        if (mPhoneReceiver != null) {
            mPhoneReceiver.unRegister();
        }

        mP.onDestroy();

        YSLog.d(TAG, "liveroomactivity WebSocketServRouter.stop");
        WebSocketServRouter.stop(this);
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
    public void onNotify(int type, Object data) {
        if (type == NotifyType.course_already_delete_record) {
            if (mLiveState) {
                mP.stopLive();
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
                mTvOnlineNum.setText(String.valueOf((int) data));
            }
            break;
            case LiveNotifyType.flow_insufficient: {
                // 流量不足警告
                if (!mIsFlowInsufficient) {
                    YSLog.d(TAG, "收到流量不足警告");
                    mIsFlowInsufficient = true;
                    mIsShowRemainingTimeTv = true;
                    showView(mTvRemainingTime);
                    mTvRemainingTime.setText(R.string.live_stream_insufficient);
                }
            }
            break;
            case LiveNotifyType.flow_run_out_of: {
                YSLog.d(TAG, "收到流量耗尽通知");
                // 流量耗尽
                if (mLiveState) {
                    mP.stopLive();
                }
                finish();
            }
            break;
            case LiveNotifyType.flow_sufficient: {
                // 流量充足
                if (mIsShowRemainingTimeTv) {
                    YSLog.d(TAG, "收到流量充足消息");
                    goneView(mTvRemainingTime);
                    mIsShowRemainingTimeTv = false;
                    mIsFlowInsufficient = false;
                }
            }
            break;
            case LiveNotifyType.live_end: {
                // 结束直播指令
                if (mLiveState) {
                    mP.stopLive();
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
                mP.stopLive();
            }
            finish();
        }
    }

    private void havePermissionState() {
        mP.initLive(this, mTextureView);
        mPhoneReceiver = new PhoneReceiver(this);
        mPhoneReceiver.setPhoneListener(this);
        mPhoneReceiver.register();
        hideView(mTvNoCameraPermission);
        showView(mTvStart);
        mIvLive.setClickable(true);
    }

    private void noPermissionState() {
        hideView(mTvStart);
        showView(mTvNoCameraPermission);
        mIvLive.setClickable(false);
        mTvState.setText(R.string.no_start);
    }

    private void switchLiveDevice() {
        mSendAcceptOrReject = false;
        YSLog.d(TAG, "直播间是否切换直播设备");
        BtnVerticalDialog dialog = new BtnVerticalDialog(this);
        dialog.setTextHint(ResLoader.getString(R.string.switch_live_record_device));
        CountDown countDown = new CountDown();
        countDown.start(5);
        dialog.addBlackButton(R.string.continue_host, view -> {
            sendWsMsg(WsOrderType.reject);
            countDown.stop();
        });
        TextView tv = dialog.addBlackButton(R.string.affirm_exit, view -> {
            // 如果在直播要先暂停，再退出页面
            if (mLiveState) {
                mP.stopLive();
                mLiveState = false;
            }
            sendWsMsg(WsOrderType.accept);
            mSendAcceptOrReject = true;
            showToast(R.string.exit_success);
            finish();
        });
        countDown.setListener(new OnCountDownListener() {

            @Override
            public void onCountDown(long remainCount) {
                if (remainCount == 0) {
                    if (mLiveState) {
                        mP.stopLive();
                        mLiveState = false;
                    }
                    sendWsMsg(WsOrderType.accept);
                    mSendAcceptOrReject = true;
                    dialog.dismiss();
                    showToast(R.string.exit_success);
                    finish();
                    return;
                }
                tv.setText(String.format(getString(R.string.affirm_exit), remainCount));
            }

            @Override
            public void onCountDownErr() {
            }
        });
        dialog.setOnDismissListener(dialogInterface -> {
            if (!mSendAcceptOrReject) {
                sendWsMsg(WsOrderType.reject);
                countDown.stop();
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

    @Override
    public void callStateRinging() {
        if (mLiveState) {
            mP.stopLive();
        }
    }

    @Override
    public void callStateIdle() {

    }

    @Override
    public void callStateOffHook() {

    }

    private class View implements LiveVideoContract.V {

        @Override
        public void setLiveTime() {
            ++mLiveTime;
            mTvLiveTime.setText(Util.getSpecialTimeFormat(mLiveTime, "'", "''"));
        }

        @Override
        public void setCountDownRemind(int i) {
            if (!mIsFlowInsufficient) {
                if (!mIsShowRemainingTimeTv) {
                    mIsShowRemainingTimeTv = true;
                    showView(mTvRemainingTime);
                }
                if (i >= 60) {
                    mTvRemainingTime.setText(String.format(getString(R.string.live_stop_remind_minute), i / 60));
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
            if (mLiveVideoState == LiveState.un_start) {
                mLiveVideoState = LiveState.live;
            }
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
        public void finishLive() {
            if (mLiveState) {
                mP.stopLive();
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
