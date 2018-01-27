package jx.csp.ui.activity.record;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
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
import jx.csp.ui.activity.main.StarActivityRouter;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.ui.frag.record.RecordImgFragRouter;
import jx.csp.ui.frag.record.RecordVideoFragRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.receiver.ConnectionReceiver.TConnType;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;
import lib.ys.util.res.ResLoader;

/**
 * 直播语音
 *
 * @author CaiXiang
 * @since 2017/10/10
 */
@Route
public class LiveAudioActivity extends BaseRecordActivity {

    private LiveAudioContract.P mLiveRecordPresenterImpl;
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mIsReceiveFlowInsufficient = false; // 是否已经收到过流量不足警告
    private int mLastPage = 0; // 上一页的位置
    private int mFirstClickStart = 1; // 是否第一次点击开始直播  0表示不是 1表示是
    private boolean mSendAcceptOrReject = false;  // 是否已经发送过同意或拒绝被踢指令

    private View mView;
    private String mAudioFilePath; // 正在录制的音频文件名字
    private int mRecordTime = 0; // 每页ppt录制的时间
    private int mLiveTotalTime = 0;  // 直播的总时长
    private boolean mAlreadyLive = false;  // 直播是否已经开始过

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
//        getDecorView().setBackgroundResource(R.color.app_nav_bar_bg);
        refresh(RefreshWay.dialog);
        mLiveRecordPresenterImpl.getData(mCourseId);

        mStarBar.setStartListener(() -> {
            mSlideFinish = true;
            mStarBar.restoration();
            mView.finishLive(true);
        });
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
                pauseLiveDialog(f,false);
            } else {
                if (mAlreadyLive) {
                    startLiveOperation(f, filePath);
                } else {
                    CommonDialog1 dialog = new CommonDialog1(this);
                    dialog.setTitle(R.string.dialog_title_hint);
                    dialog.setContent(R.string.click_start_live_now_start_live);
                    dialog.addBlackButton(R.string.cancel);
                    dialog.addBlackButton(R.string.start_live_now, v -> {
                        notify(Notifier.NotifyType.start_live, mCourseId);
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
            mLiveRecordPresenterImpl.startLiveRecord(filePath, true);
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
    protected void onDestroy() {
        String[] s = (mTvRecordTime.getText().toString()).split(":");
        StringBuffer sb = new StringBuffer();
        if (s.length == 2) {
            sb.append(s[0])
                    .append("'")
                    .append(s[1])
                    .append("''");
            YSLog.d(TAG, "sb = " + sb.toString());
            Meet meet = new Meet();
            meet.put(TMeet.id, mCourseId);
            meet.put(TMeet.playTime, sb.toString());
            notify(NotifyType.total_time, meet);
        }

        super.onDestroy();
        mLiveRecordPresenterImpl.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mLiveState) {
            pauseLiveDialog(getItem(getCurrPosition()), true);
        } else {
            finish();
        }
    }

    @Override
    protected void pageSelected(int position) {
        YSLog.d(TAG, "page change time start = " + System.currentTimeMillis());
        if (mRecordPermissionState) {
            // 在直播的时候翻页要先停止录音然后上传音频文件，再重新开始录音
            if (mLiveState) {
                // 如果上一页是的录音页面， 录音时间小于3秒 不发同步指令  在视频页面要发同步指令
                // 在直播的时候翻页,如果上一页是视频，则不掉stopLiveRecord()这个方法 要告诉服务器是翻的视频页
                Fragment f1 = getItem(mLastPage);
                if (f1 instanceof RecordImgFrag) {
                    mLiveRecordPresenterImpl.stopLiveRecord(false);
                    // 上传上一页的音频 确保存在
                    YSLog.d(TAG, "f1 time 1 = " + System.currentTimeMillis());
                    uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
                    YSLog.d(TAG, "f1 time 2 = " + System.currentTimeMillis());
                } else {
                    YSLog.d(TAG, "翻页上一页是视频 调用接口 视频 last pos = " + mLastPage);
                    mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(mLastPage).getString(TCourseDetail.id));
                    YSLog.d(TAG, "f1 time 3 = " + System.currentTimeMillis());
                }
                // 如果下一页是视频则不要录音，但页面状态是显示在直播状态，视频页滑到其他页不要上传音频
                Fragment f2 = getItem(position);
                if (f2 instanceof RecordImgFrag) {
                    YSLog.d(TAG, "f2 time 1 = " + System.currentTimeMillis());
                    String filePath = CacheUtil.createAudioFile(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id));
                    mLiveRecordPresenterImpl.startLiveRecord(filePath, false);
                    YSLog.d(TAG, "f2 time 2 = " + System.currentTimeMillis());
                } else {
                    mView.startRecordState();
                    YSLog.d(TAG, "f2 time 3 = " + System.currentTimeMillis());
                }
                mLiveRecordPresenterImpl.changePage(position);
            }
            // 记录位置
            mLastPage = position;
        } else {
            mIvRecordState.setClickable(false);
        }
        YSLog.d(TAG, "page change time end = " + System.currentTimeMillis());
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
                if (getItem(getCurrPosition()) instanceof RecordImgFrag) {
                    mLiveRecordPresenterImpl.stopLiveRecord(false);
                    uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
                }
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
                        if (getItem(getCurrPosition()) instanceof RecordImgFrag) {
                            mLiveRecordPresenterImpl.stopLiveRecord(false);
                            uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
                        }
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
                mLiveRecordPresenterImpl.stopLiveRecord(true);
                uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
            } else {
                mView.stopRecordState();
            }
        }
    }

    // 接收websocket的指令
    @SuppressLint("SwitchIntDef")
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
                    mLiveRecordPresenterImpl.stopLiveRecord(false);
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

    private void pauseLiveDialog(Fragment f, boolean finish) {
        CommonDialog2 dialog = new CommonDialog2(this);
        dialog.setHint(R.string.live_will_pause);
        dialog.addBlackButton(R.string.cancel, null);
        dialog.addBlackButton(R.string.affirm, v -> {
            // 如果当前页是视频页面，则不调stopLiveRecord()这个方法,显示停止状态
            // 如果停止的时候是在音频页面要上传音频
            if (f instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.stopLiveRecord(true);
                uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
            } else {
                mView.stopRecordState();
                YSLog.d(TAG, "暂停的时候是视频 调用接口 视频 pos = " + getCurrPosition());
                mLiveRecordPresenterImpl.uploadVideoPage(mCourseId, mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.id));
            }
            if (finish) {
                finish();
            }
        });
        dialog.show();
    }

    private class View implements LiveAudioContract.V {

        @Override
        public void setData(JoinMeeting joinMeeting) {
            stopRefresh();
            String wsUrl = joinMeeting.getString(TJoinMeeting.wsUrl);
            Course c = joinMeeting.get(TJoinMeeting.course);
            if (c != null) {
                mCourseDetailList = c.getList(TCourse.details);
            } else {
                mCourseDetailList = new ArrayList<>();
            }
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

            // 链接websocket
            if (TextUtil.isNotEmpty(wsUrl)) {
                WebSocketServRouter.create(wsUrl).route(LiveAudioActivity.this);
            }
            // 拼接分享和星评参数
            mShareAndStarArg = new Meet();
            mShareAndStarArg.put(TMeet.id, mCourseId);
            mShareAndStarArg.put(TMeet.title, ((Course) joinMeeting.getObject(TJoinMeeting.course)).getString(TCourse.title));
            mShareAndStarArg.put(TMeet.coverUrl, mCourseDetailList.get(0).getString(TCourseDetail.imgUrl));
            mShareAndStarArg.put(TMeet.playType, ((Course) joinMeeting.getObject(TJoinMeeting.course)).getInt(TCourse.playType));
            Live live = ((Live) joinMeeting.getObject(TJoinMeeting.live));
            mShareAndStarArg.put(TMeet.liveState, live.getInt(TLive.liveState));
            mAlreadyLive = live.getInt(TLive.liveState) != LiveState.un_start;
            mStarState = ((Course) joinMeeting.getObject(TJoinMeeting.course)).getBoolean(TCourse.starRateFlag);
            YSLog.d(TAG, " liveState = " + live.getInt(TLive.liveState));
            mShareAndStarArg.put(TMeet.starRateFlag, mStarState);
            mShareAndStarArg.put(TMeet.password, ((Course) joinMeeting.getObject(TJoinMeeting.course)).getString(TCourse.password));

            long serverTime = joinMeeting.getLong(TJoinMeeting.serverTime);
            long startTime;

            if (mAlreadyLive) {
                startTime = live.getLong(TLive.startTime);
                mLiveTotalTime = (int) ((serverTime - startTime) / 1000);
                if (mLiveTotalTime < 0) {
                    mLiveTotalTime = 0;
                }
                mTvRecordTime.setText(Util.getSpecialTimeFormat(mLiveTotalTime, ":", ""));
            } else {
                showView(mTvRemind);
                mTvRemind.setText(R.string.click_start_live_audio);
                startTime = serverTime;
            }
            mShareAndStarArg.put(TMeet.startTime, startTime);
            YSLog.d(TAG, "直播是否已经开始过 = " + mAlreadyLive);
            YSLog.d(TAG, "mLiveTotalTime = " + mLiveTotalTime);

            YSLog.d(TAG, "mServerTime = " + TimeFormatter.milli(serverTime, TimeFormat.from_y_24));
            YSLog.d(TAG, "mStartTime = " + TimeFormatter.milli(startTime, TimeFormat.from_y_24));

            int countDownTime = (int) ((((startTime + TimeUnit.DAYS.toMillis(1)) - serverTime) / 1000));
            if (countDownTime >= 0) {
                mLiveRecordPresenterImpl.startCountDown(countDownTime);
            }

            // 先判断以前是否直播过，直播过的话要跳到对应的页面
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Live live = (Live) joinMeeting.getObject(TJoinMeeting.live);
                    int page = live.getInt(TLive.livePage);
                    if (page > 0) {
                        setCurrPosition(page, false);
                    }
                    if (mStarState) {
                        mStarBar.setText(getString(R.string.start_star));
                        mStarBar.setThumb(R.drawable.record_ic_have_star);
                    } else {
                        mStarBar.setText(getString(R.string.slide_end));
                        mStarBar.setThumb(R.drawable.record_ic_no_star);
                    }
                    if (mCourseDetailList.size() == 1) {
                        showView(mStarBar);
                    }
                    removeOnGlobalLayoutListener(this);
                }
            });
            invalidate();
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
            mLiveRecordPresenterImpl.setLiveState(true);
            mIvRecordState.setSelected(true);
            showView(mIvRecordStateAlpha);
            mIvRecordStateAlpha.startAnimation(mAnimationFadeIn);
            mManager.notify(KNotifyId, mBuilder.build());
        }

        @Override
        public void setRecordTime(int time) {
            mRecordTime = time;
        }

        @Override
        public void stopRecordState() {
            mLiveState = false;
            mLiveRecordPresenterImpl.setLiveState(false);
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
            stopRecordState();
            LiveAudioActivity.this.showToast(id);
        }

        @Override
        public void finishLive(boolean toStar) {
            if (mLiveState && getItem(getCurrPosition()) instanceof RecordImgFrag) {
                if (mLiveState) {
                    mLiveRecordPresenterImpl.stopLiveRecord(false);
                    uploadAudioFile(mCourseId, mLastPage, CourseType.ppt_live, mAudioFilePath, mRecordTime);
                }
            }
            if (toStar) {
                StarActivityRouter.create(mShareAndStarArg).route(LiveAudioActivity.this);
            }
            finish();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {
            LiveAudioActivity.this.setViewState(state);
        }
    }
}
