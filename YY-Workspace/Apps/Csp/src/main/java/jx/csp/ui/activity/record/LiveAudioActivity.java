package jx.csp.ui.activity.record;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.LiveAudioContract;
import jx.csp.dialog.BtnVerticalDialog;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Course.TCourse;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import jx.csp.model.meeting.Live;
import jx.csp.model.meeting.Live.TLive;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.presenter.LiveAudioPresenterImpl;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.ui.frag.record.RecordImgFragRouter;
import jx.csp.ui.frag.record.RecordVideoFragRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.receiver.ConnectionReceiver.TConnType;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * 直播语音
 *
 * @author CaiXiang
 * @since 2017/10/10
 */
@Route
public class LiveAudioActivity extends BaseRecordActivity {

    private LiveAudioPresenterImpl mLiveRecordPresenterImpl;
    private boolean mBeginCountDown = false;  // 是否开始倒计时,直播时间到了才开始
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mStopCountDown = false; // 是否开始进行结束倒计时
    private boolean mIsReceiveFlowInsufficient = false; // 是否已经收到过流量不足警告
    private int mLastPage = 0; // 上一页的位置
    private int mFirstClickStart = 1; // 是否第一次点击开始直播  0表示不是 1表示是
    private boolean mSendAcceptOrReject = false;  // 是否已经发送过同意或拒绝被踢指令

    @Arg(opt = true)
    long mStartTime;
    @Arg(opt = true)
    long mStopTime;
    long mRealStopTime;

    private View mView;
    private String mFilePath; // 正在录制的音频文件名字
    private long mServerTime; // 服务器时间

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mRealStopTime = mStopTime + TimeUnit.MINUTES.toMillis(15);
        mView = new View();
        mLiveRecordPresenterImpl = new LiveAudioPresenterImpl(mView);
    }

    @Override
    public void setViews() {
        super.setViews();

        // 检查录音权限
        if (checkPermission(KMicroPermissionCode, Permission.micro_phone)) {
            havePermissionState();
        } else {
            noPermissionState();
        }
        showView(mLayoutOnline);
        showView(mTvStartRemain);
        mIvRecordState.setBackgroundResource(R.drawable.record_selector_state);
        setOnClickListener(R.id.record_iv_state);

        //请求网络
        refresh(RefreshWay.dialog);
        mLiveRecordPresenterImpl.getData(mCourseId);
    }

    @Override
    protected void onClick(int id) {
        if (Util.noNetwork()) {
            return;
        }
        if (id == R.id.record_iv_state) {
            String filePath = CacheUtil.getAudioPath(mCourseId, getCurrPosition());
            // 判断直播时间是否已经到了
            if (mBeginCountDown) {
                Fragment f = getItem(getCurrPosition());
                if (mLiveState) {
                    // 如果当前页是视频页面，则不调stopLiveRecord()这个方法,显示停止状态
                    // 如果停止的时候是在音频页面要上传音频
                    if (f instanceof RecordImgFrag) {
                        mLiveRecordPresenterImpl.stopLiveRecord();
                        uploadAudioFile(mCourseId, mLastPage, PlayType.live, mFilePath);
                    } else {
                        mView.stopRecordState();
                        YSLog.d(TAG, "暂停的时候是视频 调用接口 视频 pos = " + getCurrPosition());
                        mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.id));
                    }
                } else {
                    // 如果点击开始直播是在视频页，不需要录音，在直播状态
                    if (f instanceof RecordImgFrag) {
                        mLiveRecordPresenterImpl.startLiveRecord(filePath);
                    } else {
                        mView.startRecordState();
                    }
                    // 点击开始直播告诉服务器开始直播，自己不发同步指令
                    mLiveRecordPresenterImpl.startLive(mCourseId,
                            mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.videoUrl),
                            mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.imgUrl),
                            mFirstClickStart, getCurrPosition());
                    mFirstClickStart = 0;
                    YSLog.d(TAG, "点击开始直播,延时3s请求服务器发同步指令" + getCurrPosition());
                }
            } else {
                startCountDownAndLive(filePath);
            }
        }
    }

    @Override
    protected void skipToLast() {
        setCurrPosition(getCurrPosition() - KOne);
    }

    @Override
    protected void skipToNext() {
        setCurrPosition(getCurrPosition() + KOne);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLiveState) {
            // 判断当前页面是视频还是ppt
            Fragment f = getItem(getCurrPosition());
            if (f instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                uploadAudioFile(mCourseId, mLastPage, PlayType.live, mFilePath);
            } else {
                mView.stopRecordState();
                YSLog.d(TAG, "onPause的时候是视频 调用接口 视频 pos = " + getCurrPosition());
                mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.id));
            }
            mLiveState = false;
        }
        mIvRecordState.setSelected(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLiveRecordPresenterImpl.onDestroy();
    }

    @Override
    protected void pageSelected(int position) {
        // 在直播的时候翻页要先停止录音然后上传音频文件，再重新开始录音
        if (mLiveState) {
            mLiveRecordPresenterImpl.changePage(position);
            // 如果上一页是的录音页面， 录音时间小于3秒 不发同步指令  在视频页面要发同步指令
            // 在直播的时候翻页,如果上一页是视频，则不掉stopLiveRecord()这个方法 要告诉服务器是翻的视频页
            Fragment f1 = getItem(mLastPage);
            if (f1 instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                // 上传上一页的音频 确保存在
                uploadAudioFile(mCourseId, mLastPage, PlayType.live, mFilePath);
            } else {
                YSLog.d(TAG, "翻页上一页是视频 调用接口 视频 last pos = " + mLastPage);
                mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(mLastPage).getString(TCourseDetail.id));
            }
            // 如果下一页是视频则不要录音，但页面状态是显示在直播状态，视频页滑到其他页不要上传音频
            Fragment f2 = getItem(position);
            if (f2 instanceof RecordImgFrag) {
                String filePath = CacheUtil.getAudioPath(mCourseId, position);
                mLiveRecordPresenterImpl.startLiveRecord(filePath);
            } else {
                mView.startRecordState();
            }
        }
        // 记录位置
        mLastPage = position;
    }

    @Override
    protected void switchDevice() {
        mSendAcceptOrReject = false;
        YSLog.d(TAG, "是否切换直播设备");
        BtnVerticalDialog dialog = new BtnVerticalDialog(this);
        dialog.setTextHint(ResLoader.getString(R.string.switch_live_record_device));
        CountDown countDown = new CountDown();
        countDown.start(5);
        dialog.addBlackButton(R.string.continue_host, view -> {
            notifyServ(LiveNotifyType.send_msg, WsOrderType.reject);
            countDown.stop();
        });
        TextView tv = dialog.addBlueButton(R.string.affirm_exit, view -> {
            // 如果在直播要先暂停录音，然后上传音频，再退出页面
            if (mLiveState) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                mLiveState = false;
                uploadAudioFile(mCourseId, mLastPage, PlayType.live, mFilePath);
            }
            notifyServ(LiveNotifyType.send_msg, WsOrderType.accept);
            mSendAcceptOrReject = true;
            finish();
        });
        countDown.setListener(new OnCountDownListener() {

            @Override
            public void onCountDown(long remainCount) {
                if (remainCount == 0) {
                    if (mLiveState) {
                        mLiveRecordPresenterImpl.stopLiveRecord();
                        uploadAudioFile(mCourseId, mLastPage, PlayType.live, mFilePath);
                        mLiveState = false;
                    }
                    notifyServ(LiveNotifyType.send_msg, WsOrderType.accept);
                    mSendAcceptOrReject = true;
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
        dialog.setOnDismissListener(dialogInterface -> {
            if (!mSendAcceptOrReject) {
                YSLog.d(TAG, "发送拒绝指令");
                notifyServ(LiveNotifyType.send_msg, WsOrderType.reject);
                countDown.stop();
            }
        });
        dialog.show();
    }

    @Override
    protected void onCallOffHooK() {
        if (mLiveState) {
            // 判断当前页是不是视频
            if (getItem(getCurrPosition()) instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                uploadAudioFile(mCourseId, mLastPage, PlayType.live, mFilePath);
            } else {
                mView.stopRecordState();
            }
        }
    }

    // 接收websocket的指令
    @Override
    public void onLiveNotify(int type, Object data) {
        switch (type) {
            case LiveNotifyType.sync: {
                int page = (int) data;
                mLiveRecordPresenterImpl.setWsPos(page);
                setCurrPosition(page);
            }
            break;
            case LiveNotifyType.inquired: {
                switchDevice();
            }
            break;
            case LiveNotifyType.online_num: {
                mTvOnlineNum.setText(String.valueOf((int) data));
            }
            break;
            case LiveNotifyType.flow_insufficient: {
                // 接收到流量不足警告
                if (!mIsReceiveFlowInsufficient) {
                    CommonDialog2 dialog = new CommonDialog2(this);
                    dialog.setHint(R.string.record_live_insufficient);
                    dialog.addBlueButton(R.string.ok);
                    dialog.show();
                    mIsReceiveFlowInsufficient = true;
                }
            }
            break;
        }
    }

    @Override
    public void onPermissionResult(int code, int result) {
        if (code == KMicroPermissionCode) {
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
    public void onConnectChanged(TConnType type) {
        YSLog.d(TAG, "onConnectChanged type = " + type);
        // 没有网络时的处理
        if (type == TConnType.disconnect) {
            showToast(R.string.network_disabled);
            // 如果在录音页面要停止录音，视频页面不要
            Fragment f = getItem(getCurrPosition());
            if (mLiveState) {
                if (f instanceof RecordImgFrag) {
                    mLiveRecordPresenterImpl.stopLiveRecord();
                }
            }
            finish();
        }
    }

    private void havePermissionState() {
        initPhoneCallingListener();
        goneView(mTvStartRemain);
        mIvRecordState.setClickable(true);
        mIvVoiceState.setSelected(true);
    }

    private void noPermissionState() {
        showView(mTvStartRemain);
        mTvStartRemain.setText(R.string.no_record_permission);
        mIvRecordState.setClickable(false);
        mIvVoiceState.setSelected(false);
    }

    private void startCountDownAndLive(String filePath) {
        if (mServerTime >= mStartTime) {
            mBeginCountDown = true;
            mLiveRecordPresenterImpl.startCountDown(mStartTime, mRealStopTime, mServerTime);
            // 如果点击开始直播是在视频页，不需要录音，在直播状态
            if (getItem(getCurrPosition()) instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.startLiveRecord(filePath);
            } else {
                mView.startRecordState();
            }
            // 点击开始直播告诉服务器开始直播，自己不发同步指令
            mLiveRecordPresenterImpl.startLive(mCourseId,
                    mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.videoUrl),
                    mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.imgUrl),
                    mFirstClickStart, getCurrPosition());
            mFirstClickStart = 0;
            YSLog.d(TAG, "点击开始直播,延时3s请求服务器发同步指令" + getCurrPosition());
        } else {
            showToast(R.string.meeting_no_start_remain);
        }
    }

    private class View implements LiveAudioContract.V {

        @Override
        public void setData(JoinMeeting joinMeeting) {
            stopRefresh();
            String wsUrl = joinMeeting.getString(TJoinMeeting.wsUrl);
            mServerTime = joinMeeting.getLong(TJoinMeeting.serverTime);
            mCourseDetailList = joinMeeting.get(TJoinMeeting.course).getList(TCourse.details);
            SparseArray<String> courseDetailIdArray = new SparseArray<>();
            mTvTotalPage.setText(String.valueOf(mCourseDetailList.size()));
            for (int i = 0; i < mCourseDetailList.size(); ++i) {
                CourseDetail courseDetail = mCourseDetailList.get(i);
                courseDetailIdArray.put(i, courseDetail.getString(TCourseDetail.id));
                // 判断是视频还是图片
                if (TextUtil.isEmpty(courseDetail.getString(TCourseDetail.videoUrl))) {
                    add(RecordImgFragRouter
                            .create(courseDetail.getString(TCourseDetail.imgUrl))
                            .route());
                } else {
                    add(RecordVideoFragRouter
                            .create(courseDetail.getString(TCourseDetail.videoUrl), courseDetail.getString(TCourseDetail.imgUrl))
                            .route());
                }
            }
            mAudioUploadPresenter.setCourseDetailIdArray(courseDetailIdArray);
            // 先判断以前是否直播过，直播过的话要跳到对应的页面
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Live live = (Live) joinMeeting.getObject(TJoinMeeting.live);
                    int page = live.getInt(TLive.livePage);
                    if (page != 0) {
                        setCurrPosition(page, false);
                    }

                    removeOnGlobalLayoutListener(this);
                }
            });
            invalidate();
            // 链接websocket
            if (TextUtil.isNotEmpty(wsUrl)) {
                WebSocketServRouter.create(wsUrl).route(LiveAudioActivity.this);
            }
            // 判断直播是否已经开始
            if (mServerTime >= mStartTime) {
                mBeginCountDown = true;
                mLiveRecordPresenterImpl.startCountDown(mStartTime, mRealStopTime, mServerTime);
                mTvStartRemain.setText(R.string.meeting_start_click_start_live);
            } else {
                setNavBarMidText(mTitle);
                mTvStartRemain.setText(R.string.meeting_no_start_remain);
            }
        }

        @Override
        public void sendSyncInstruction(int pos) {
            notifyServ(LiveNotifyType.send_msg, pos, WsOrderType.sync);
        }

        @Override
        public void setLiveTime(String str) {
            setNavBarMidText(str);
        }

        @Override
        public void startRecordState() {
            goneView(mTvStartRemain);
            mLiveState = true;
            mIvRecordState.setSelected(true);
            if (mStopCountDown) {
                mTvRecordState.setText(R.string.record_live_stop);
            } else {
                mTvRecordState.setText(R.string.live);
            }
        }

        @Override
        public void stopRecordState() {
            showView(mTvStartRemain);
            mLiveState = false;
            mIvRecordState.setSelected(false);
            mTvRecordState.setText(R.string.record_live_start);
        }

        @Override
        public void setCountDownRemain(boolean show, long l) {
            if (show) {
                showView(mTvTimeRemain);
            }
            if (l >= 60) {
                mTvTimeRemain.setText(String.format(getString(R.string.live_stop_remind_minute), l / 60));
            } else {
                mTvTimeRemain.setText(String.format(getString(R.string.live_stop_remind_second), l));
            }
        }

        @Override
        public void joinUploadRank(String audioFilePath) {
            uploadAudioFile(mCourseId, getCurrPosition(), PlayType.live, audioFilePath);
        }

        @Override
        public void setAudioFilePath(String filePath) {
            mFilePath = filePath;
        }

        @Override
        public void changeRecordIvRes() {
            mStopCountDown = true;
            mIvRecordState.setImageResource(R.drawable.record_selector_live_state_warm);
            mTvRecordState.setTextColor(ResLoader.getColor(R.color.text_d0011b));
            mTvNavBar.setTextColor(ResLoader.getColor(R.color.text_d0011b));
            if (mLiveState) {
                mTvRecordState.setText(R.string.record_live_stop);
            }
        }

        @Override
        public void showToast(int id) {
            stopRecordState();
            LiveAudioActivity.this.showToast(id);
        }

        @Override
        public void finishLive() {
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
