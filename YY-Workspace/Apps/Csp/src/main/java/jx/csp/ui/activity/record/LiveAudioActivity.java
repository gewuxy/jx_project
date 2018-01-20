package jx.csp.ui.activity.record;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Route;
import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.LiveAudioContract;
import jx.csp.dialog.BtnVerticalDialog;
import jx.csp.dialog.CommonDialog1;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Course.TCourse;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import jx.csp.model.meeting.Live;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Live.TLive;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.presenter.LiveAudioPresenterImpl;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.ui.frag.record.RecordImgFragRouter;
import jx.csp.ui.frag.record.RecordVideoFragRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.receiver.ConnectionReceiver.TConnType;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;

/**
 * 直播语音
 *
 * @author CaiXiang
 * @since 2017/10/10
 */
@Route
public class LiveAudioActivity extends BaseRecordActivity {

    private LiveAudioPresenterImpl mLiveRecordPresenterImpl;
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mLiveStopRemind = false; // 结束直播提醒
    private boolean mIsReceiveFlowInsufficient = false; // 是否已经收到过流量不足警告
    private int mLastPage = 0; // 上一页的位置
    private int mFirstClickStart = 1; // 是否第一次点击开始直播  0表示不是 1表示是
    private boolean mSendAcceptOrReject = false;  // 是否已经发送过同意或拒绝被踢指令

    private View mView;
    private String mAudioFilePath; // 正在录制的音频文件名字
    private int mRecordTime = 0; // 每页ppt录制的时间
    private int mLiveTotalTime = 0;  // 直播的总时长
    private boolean mAlreadyLive = false;  // 直播是否已经开始过
    private boolean mStarState = false;  // 是否有星评

    private long mServerTime; // 服务器时间
    private long mStartTime;  // 开始时间

    @Override
    public void initData() {
        super.initData();

        mView = new View();
        mLiveRecordPresenterImpl = new LiveAudioPresenterImpl(mView);
        mBuilder.setContentText(getString(R.string.live));
        Intent intent = new Intent(App.getContext(), LiveAudioActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
    }

    @Override
    public void setViews() {
        super.setViews();

        showView(mLayoutOnline);
        goneView(mLayoutTvRecordState);
        goneView(mIvAudition);
        goneView(mIvRerecording);
        mIvRecordState.setImageResource(R.drawable.live_selector_record_state);
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
            String filePath = CacheUtil.createAudioFile(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id));
            // 判断直播是否已经开始过
            Fragment f = getItem(getCurrPosition());
            if (mLiveState) {
                CommonDialog2 dialog = new CommonDialog2(this);
                dialog.setHint(R.string.live_will_pause);
                dialog.addBlackButton(R.string.cancel, null);
                dialog.addBlackButton(R.string.affirm, v -> {
                    // 如果当前页是视频页面，则不调stopLiveRecord()这个方法,显示停止状态
                    // 如果停止的时候是在音频页面要上传音频
                    if (f instanceof RecordImgFrag) {
                        mLiveRecordPresenterImpl.stopLiveRecord();
                        uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
                    } else {
                        mView.stopRecordState(0);
                        YSLog.d(TAG, "暂停的时候是视频 调用接口 视频 pos = " + getCurrPosition());
                        mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.id));
                    }
                });
                dialog.show();
            } else {
                if (mAlreadyLive) {
                    startLiveOperation(f, filePath);
                } else {
                    CommonDialog1 dialog = new CommonDialog1(this);
                    dialog.setTitle(R.string.dialog_title_hint);
                    dialog.setContent(R.string.click_start_live_now_start_live);
                    dialog.addBlackButton(R.string.cancel);
                    dialog.addBlackButton(R.string.start_live_now, v -> {
                        hideView(mTvRemind);
                        mAlreadyLive = true;
                        startLiveOperation(f, filePath);
                    });
                    dialog.show();
                }
            }
        }
    }

    private void startLiveOperation(Fragment f, String filePath) {
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

    @Override
    protected void onPause() {
        super.onPause();

//        if (mLiveState) {
//            // 判断当前页面是视频还是ppt
//            Fragment f = getItem(getCurrPosition());
//            if (f instanceof RecordImgFrag) {
//                mLiveRecordPresenterImpl.stopLiveRecord();
//                uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
//            } else {
//                mView.stopRecordState(0);
//                YSLog.d(TAG, "onPause的时候是视频 调用接口 视频 pos = " + getCurrPosition());
//                mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.id));
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLiveRecordPresenterImpl.onDestroy();
    }

    @Override
    protected void pageSelected(int position) {
        if (mRecordPermissionState) {
            // 在直播的时候翻页要先停止录音然后上传音频文件，再重新开始录音
            if (mLiveState) {
                // 如果上一页是的录音页面， 录音时间小于3秒 不发同步指令  在视频页面要发同步指令
                // 在直播的时候翻页,如果上一页是视频，则不掉stopLiveRecord()这个方法 要告诉服务器是翻的视频页
                Fragment f1 = getItem(mLastPage);
                if (f1 instanceof RecordImgFrag) {
                    mLiveRecordPresenterImpl.stopLiveRecord();
                    // 上传上一页的音频 确保存在
                    uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
                } else {
                    YSLog.d(TAG, "翻页上一页是视频 调用接口 视频 last pos = " + mLastPage);
                    mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(mLastPage).getString(TCourseDetail.id));
                }
                // 如果下一页是视频则不要录音，但页面状态是显示在直播状态，视频页滑到其他页不要上传音频
                Fragment f2 = getItem(position);
                if (f2 instanceof RecordImgFrag) {
                    String filePath = CacheUtil.createAudioFile(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id));
                    mLiveRecordPresenterImpl.startLiveRecord(filePath);
                } else {
                    mView.startRecordState();
                }
                mLiveRecordPresenterImpl.changePage(position);
            }
            // 记录位置
            mLastPage = position;
        } else {
            mIvRecordState.setClickable(false);
        }
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
        TextView tv = dialog.addBlackButton(R.string.affirm_exit, view -> {
            // 如果在直播要先暂停录音，然后上传音频，再退出页面
            if (mLiveState) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
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
                        uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
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
    protected void startAnimationFadeIn() {
        if (mLiveState) {
            mIvRecordStateAlpha.startAnimation(mAnimationFadeIn);
        }
    }

    @Override
    protected void startAnimationFadeOut() {
        if (mLiveState) {
            mIvRecordStateAlpha.startAnimation(mAnimationFadeOut);
        }
    }

    @Override
    protected void onCallRinging() {
        if (mLiveState) {
            // 判断当前页是不是视频
            if (getItem(getCurrPosition()) instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
            } else {
                mView.stopRecordState(0);
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
                    dialog.setHint(R.string.live_stream_insufficient);
                    dialog.addBlackButton(R.string.ok);
                    dialog.show();
                    mIsReceiveFlowInsufficient = true;
                }
            }
            break;
            case LiveNotifyType.reject: {
                YSLog.d(TAG, "录音页面-接收到拒绝进入指令");
                finish();
            }
            break;
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
                    mLiveRecordPresenterImpl.onlyStopRecord();
                    // 删除文件
                    boolean b = FileUtil.delFile(new File(mAudioFilePath));
                    YSLog.d(TAG, "无网络后删除正在录制的文件是否成功 = " + b);
                }
            }
            finish();
        }
    }

    @Override
    public void havePermissionState() {
        initPhoneCallingListener();
        mIvRecordState.setClickable(true);
    }

    @Override
    public void noPermissionState() {
        mIvRecordState.setClickable(false);
        noRecordPermissionDialog();
    }

    private class View implements LiveAudioContract.V {

        @Override
        public void setData(JoinMeeting joinMeeting) {
            stopRefresh();
            String wsUrl = joinMeeting.getString(TJoinMeeting.wsUrl);
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
            // 拼接分享参数
            mShareArguments = new Meet();
            mShareArguments.put(TMeet.id, mCourseId);
            mShareArguments.put(TMeet.title, ((Course) joinMeeting.getObject(TJoinMeeting.course)).getString(TCourse.title));
            mShareArguments.put(TMeet.coverUrl, mCourseDetailList.get(0).getString(TCourseDetail.imgUrl));
            mShareArguments.put(TMeet.playType, ((Course) joinMeeting.getObject(TJoinMeeting.course)).getInt(TCourse.playType));
            Live live = ((Live) joinMeeting.getObject(TJoinMeeting.live));
            mShareArguments.put(TMeet.liveState, live.getInt(TLive.liveState));
            mAlreadyLive = live.getInt(TLive.liveState) == LiveState.un_start ? false : true;
            YSLog.d(TAG, " liveState = " + live.getInt(TLive.liveState));

            mServerTime = joinMeeting.getLong(TJoinMeeting.serverTime);
            mStartTime = live.getLong(TLive.startTime);

            if (mAlreadyLive) {
                mLiveTotalTime = (int) ((mServerTime - mStartTime) / 1000);
                mTvRecordTime.setText(Util.getSpecialTimeFormat(mLiveTotalTime, ":", ""));
            } else {
                showView(mTvRemind);
                mTvRemind.setText(R.string.click_start_live_audio);
            }
            YSLog.d(TAG, "直播是否已经开始过 = " + mAlreadyLive);
            YSLog.d(TAG, "mLiveTotalTime = " + mLiveTotalTime);

            int countDownTime = (int) ((((mStartTime + TimeUnit.DAYS.toMillis(1)) - mServerTime) / 1000));
            mLiveRecordPresenterImpl.startCountDown(countDownTime);
        }

        @Override
        public void sendSyncInstruction(int pos) {
            notifyServ(LiveNotifyType.send_msg, pos, WsOrderType.sync);
        }

        @Override
        public void setLiveTime() {
            ++mLiveTotalTime;
            mTvRecordTime.setText(Util.getSpecialTimeFormat(mLiveTotalTime, ":", ""));
        }

        @Override
        public void startRecordState() {
            mLiveState = true;
            mLiveRecordPresenterImpl.setLiveState(mLiveState);
            mIvRecordState.setSelected(true);
            showView(mIvRecordStateAlpha);
            mIvRecordStateAlpha.startAnimation(mAnimationFadeIn);
            mManager.notify(KNotifyId, mBuilder.build());
        }

        @Override
        public void stopRecordState(int time) {
            mRecordTime = time;
            mLiveState = false;
            mLiveRecordPresenterImpl.setLiveState(mLiveState);
            mIvRecordState.setSelected(false);
            mIvRecordStateAlpha.clearAnimation();
            goneView(mIvRecordStateAlpha);
            mManager.cancel(KNotifyId);
        }

        @Override
        public void joinUploadRank(String audioFilePath, int time) {
            uploadAudioFile(mCourseId, getCurrPosition(), CourseType.ppt_live, audioFilePath, time);
        }

        @Override
        public void setAudioFilePath(String filePath) {
            mAudioFilePath = filePath;
        }

        @Override
        public void setLiveStopRemind(int minute) {
            showView(mTvRemind);
            mTvRemind.setTextColor(ResLoader.getColor(R.color.text_ace400));
            mTvRemind.setText(String.format(getString(R.string.audio_live_stop_remind), minute));
        }

        @Override
        public void showToast(int id) {
            stopRecordState(0);
            LiveAudioActivity.this.showToast(id);
        }

        @Override
        public void finishLive() {
            if (mLiveState && getItem(getCurrPosition()) instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
            }
            finish();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {
        }
    }
}
