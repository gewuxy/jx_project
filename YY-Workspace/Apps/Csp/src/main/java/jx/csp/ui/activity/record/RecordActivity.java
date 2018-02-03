package jx.csp.ui.activity.record;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Route;
import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.RecordContract;
import jx.csp.dialog.BtnVerticalDialog;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.RecordUnusualState;
import jx.csp.model.RecordUnusualState.TRecordUnusualState;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Course.TCourse;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import jx.csp.model.meeting.Record;
import jx.csp.model.meeting.Record.TRecord;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.presenter.RecordPresenterImpl;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.main.StarActivityRouter;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.ui.frag.record.RecordImgFragRouter;
import jx.csp.ui.frag.record.RecordVideoFragRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import jx.csp.view.GestureView.onGestureViewListener;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
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
 * 普通的语音录制
 *
 * @author CaiXiang
 * @since 2017/10/10
 */
@Route
public class RecordActivity extends BaseRecordActivity implements onGestureViewListener {

    private boolean mRecordState = false; // 是否在录制中
    private boolean mPlayState = false; // 是否在播放音频
    private boolean mPlayPause = false; // 播放暂停状态
    private AnimationDrawable mAnimationRecord;
    private RecordContract.P mRecordPresenter;
    private int mWsPosition = -1;  // websocket接收到的页数
    private boolean mSendAcceptOrReject = false;  // 是否已经发送过同意或拒绝被踢指令
    private String mAudioFilePath; // 正在录制的音频文件名字

    private SparseArray<Integer> mRecordTimeArray;  // 每页ppt录制的音频时间
    private boolean mSeekBarChange = false;  // 互斥变量，防止定时器与SeekBar拖动时进度冲突
    private Vibrator mVibrator;  // 震动服务对象

    private boolean mCanContinueRecord = false;  // 能否续录
    private boolean mContinueRecord = false;  // 是否在续录

    @Override
    public void initData() {
        super.initData();

        mRecordPresenter = new RecordPresenterImpl(new View());
        mRecordTimeArray = new SparseArray<>();

        //获取手机震动服务
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        mBuilder.setContentText(getString(R.string.record_ing));
        Intent intent = new Intent(App.getContext(), RecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
    }

    @Override
    public void setViews() {
        super.setViews();

        mGestureView.setGestureViewListener(this);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekBarChange = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mRecordPresenter.mediaPlayProgress(seekBar.getProgress());
                mSeekBarChange = false;
            }
        });

        //请求网络
        refresh(RefreshWay.dialog);
        mRecordPresenter.getData(mCourseId);

        RecordUnusualState.inst().put(TRecordUnusualState.courseId, mCourseId);
        RecordUnusualState.inst().saveToSp();

        setOnClickListener(R.id.record_iv_state);
        setOnClickListener(R.id.record_iv_audition);
        setOnClickListener(R.id.record_iv_rerecording);

        mStarBar.setStartListener(() -> {
            mSlideFinish = true;
            CommonServRouter.create(ReqType.exit_record)
                    .courseId(mCourseId)
                    .pageNum(getCurrPosition())
                    .overType(1)  //  录播结束的标记位
                    .route(this);
            if (mRecordState) {
                mRecordPresenter.stopRecord();
            }
            int totalTime = 0;
            for (int i = 0; i < mRecordTimeArray.size(); ++i) {
                totalTime += mRecordTimeArray.get(i);
            }
            mShareAndStarArg.put(TMeet.playTime, Util.getSpecialTimeFormat(totalTime, "'", "''"));
            StarActivityRouter.create(mShareAndStarArg).route(RecordActivity.this);
            mStarBar.restoration();
            finish();
        });
    }

    @Override
    public void onClick(int id) {
        switch (id) {
            case R.id.record_iv_state: {
                if (mRecordState) {
                    mRecordPresenter.stopRecord();
                    showView(mTvRemind);
                    mTvRemind.setTextColor(ResLoader.getColor(R.color.text_787c86));
                    mTvRemind.setText(R.string.record_pause);
                    mTvRecordState.setText(R.string.continue_record);
                    mCanContinueRecord = true;
                } else {
                    // 在播放的时候点击录制，要先停止播放
                    stopPlayOperation();
                    String filePath = CacheUtil.createAudioFile(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id));
                    mRecordPresenter.startRecord(filePath, getCurrPosition(), mRecordTimeArray.get(getCurrPosition()));
                    mTvRecordState.setText(R.string.record);
                }
            }
            break;
            case R.id.record_iv_audition: {
                if (mPlayState) {
                    mRecordPresenter.pausePlay();
                    mIvAudition.setImageResource(R.drawable.record_ic_audition);
                    mPlayState = false;
                    mPlayPause = true;
                } else {
                    if (mPlayPause) {
                        mRecordPresenter.continuePlay(mSeekBar.getProgress());
                        mIvAudition.setImageResource(R.drawable.record_ic_audition_ing);
                        mPlayState = true;
                        mPlayPause = false;
                    } else {
                        mRecordPresenter.startPlay(CacheUtil.getExistAudioFilePath(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id)));
                    }
                }
            }
            break;
            case R.id.record_iv_rerecording: {
                CommonDialog2 dialog = new CommonDialog2(this);
                dialog.setHint(R.string.rerecording_remind);
                dialog.addButton(R.string.cancel, R.color.text_404356, null);
                dialog.addBlackButton(R.string.affirm, v -> {
                    stopPlayOperation();
                    showView(mTvRemind);
                    mTvRemind.setTextColor(ResLoader.getColor(R.color.text_787c86));
                    mTvRemind.setText(R.string.record_time_remind);
                    mTvRecordTime.setText("00:00");
                    mRecordTimeArray.put(getCurrPosition(), 0);
                    mTvRecordState.setText(R.string.record);
                    mIvAudition.setImageResource(R.drawable.record_ic_can_not_audition);
                    mIvAudition.setClickable(false);
                    mIvRerecording.setSelected(false);
                    mIvRerecording.setClickable(false);

                    mIvRecordState.setImageResource(R.drawable.animation_record);
                    mAnimationRecord = (AnimationDrawable) mIvRecordState.getDrawable();
                    mIvRecordState.setClickable(true);
                    // 删除以前的音频文件
                    String filePath = CacheUtil.getExistAudioFilePath(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id));
                    FileUtil.delFile(new File(filePath));
                    mRecordPresenter.deleteAudio(mCourseDetailList.get(getCurrPosition()).getString(TCourseDetail.id));
                });
                dialog.show();
            }
            break;
        }
    }

    @Override
    public void moveLast() {
        if (getCurrPosition() == 0) {
            return;
        }
        moveOperation();
    }

    @Override
    public void moveNext() {
        if (getCurrPosition() == (mCourseDetailList.size()) - KOne) {
            return;
        }
        moveOperation();
    }

    private void moveOperation() {
        if (mRecordState) {
            showToast(R.string.record_ing_slide_remind);
            return;
        }
        if (SpApp.inst().showSlideDialog() && mCanContinueRecord) {
            CommonDialog2 dialog = new CommonDialog2(this);
            dialog.setHint(R.string.slide_can_not_rerecording);
            dialog.addBlackButton(R.string.ok, v -> {
                SpApp.inst().neverShowSlideDialog();
                getViewPager().setScrollable(true);
                goneView(mGestureView);
            });
            dialog.show();
        }
    }

    @Override
    protected void onCallRinging() {
        if (mRecordState) {
            mRecordPresenter.stopRecord();
            showView(mTvRemind);
            mTvRemind.setTextColor(ResLoader.getColor(R.color.text_787c86));
            mTvRemind.setText(R.string.record_pause);
            mTvRecordState.setText(R.string.continue_record);
            mCanContinueRecord = true;
        }
    }

    @Override
    protected void onDestroy() {
        int totalTime = 0;
        for (int i = 0; i < mRecordTimeArray.size(); ++i) {
            totalTime += mRecordTimeArray.get(i);
        }
        Meet meet = new Meet();
        meet.put(TMeet.id, mCourseId);
        meet.put(TMeet.playTime, Util.getSpecialTimeFormat(totalTime, "'", "''"));
        notify(NotifyType.total_time, meet);
        if (mRecordState) {
            mRecordPresenter.stopRecord();
        }
        super.onDestroy();
        mRecordPresenter.onDestroy();
    }

    @Override
    protected void pageSelected(int position) {
        if (position == (mCourseDetailList.size() - 1)) {
            showView(mTvLastPageRemind);
        } else {
            goneView(mTvLastPageRemind);
        }
        if (mRecordPermissionState) {
            mCanContinueRecord = false;
            stopPlayOperation();
            changeViewState(position);
            if (position != mWsPosition) {
                notifyServ(LiveNotifyType.send_msg, position, WsOrderType.sync);
            }
        } else {
            mIvRecordState.setImageResource(R.drawable.record_ic_can_not_record);
            mIvRecordState.setClickable(false);
            mIvRerecording.setSelected(false);
            mIvRerecording.setClickable(false);
        }
    }

    // 如果页面是视频页 录音状态，试听，重录图片要变且不能点击
    private void changeViewState(int position) {
        Fragment f = getItem(position);
        if (f instanceof RecordImgFrag) {
            mTvRecordTime.setText(TimeFormatter.second(mRecordTimeArray.get(position), TimeFormat.from_m));
            mTvRecordState.setText(R.string.record);
            // 判断是否已经录制过音频，改变按钮状态
            String basePath = CacheUtil.getAudioCacheDir() + mCourseId + File.separator + mCourseDetailList.get(position).getInt(TCourseDetail.id);
            File folder = new File(basePath);
            File[] files = folder.listFiles();
            if (files.length == 0) {
                showView(mTvRemind);
                mTvRemind.setTextColor(ResLoader.getColor(R.color.text_787c86));
                mTvRemind.setText(R.string.record_time_remind);
                mIvAudition.setImageResource(R.drawable.record_ic_can_not_audition);
                mIvAudition.setClickable(false);
                mIvRecordState.setImageResource(R.drawable.animation_record);
                mAnimationRecord = (AnimationDrawable) mIvRecordState.getDrawable();
                mIvRecordState.setClickable(true);
                mIvRerecording.setSelected(false);
                mIvRerecording.setClickable(false);
            } else {
                hideView(mTvRemind);
                mIvAudition.setImageResource(R.drawable.record_ic_audition);
                mIvAudition.setClickable(true);
                mIvRecordState.setImageResource(R.drawable.record_ic_can_not_record);
                mIvRecordState.setClickable(false);
                mIvRerecording.setSelected(true);
                mIvRerecording.setClickable(true);
            }
        } else {
            videoState();
        }
    }

    @Override
    protected void switchDevice() {
        mSendAcceptOrReject = false;
        BtnVerticalDialog dialog = new BtnVerticalDialog(this);
        dialog.setTextHint(ResLoader.getString(R.string.switch_common_record_device));
        CountDown countDown = new CountDown();
        countDown.start(5);
        dialog.addBlackButton(R.string.continue_host, view -> {
            notifyServ(LiveNotifyType.send_msg, WsOrderType.reject);
            countDown.stop();
        });
        TextView tv = dialog.addBlackButton(R.string.affirm_exit, view -> {
            // 如果在直播要先暂停录音，然后上传音频，再退出页面
            if (mRecordState) {
                mRecordPresenter.stopRecord();
            }
            notifyServ(LiveNotifyType.send_msg, WsOrderType.accept);
            mSendAcceptOrReject = true;
            countDown.stop();
            showToast(R.string.exit_success);
            finish();
        });
        countDown.setListener(new OnCountDownListener() {

            @Override
            public void onCountDown(long remainCount) {
                if (remainCount == 0) {
                    if (mRecordState) {
                        mRecordPresenter.stopRecord();
                    }
                    notifyServ(LiveNotifyType.send_msg, WsOrderType.accept);
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
                notifyServ(LiveNotifyType.send_msg, WsOrderType.reject);
                countDown.stop();
            }
        });
        dialog.show();
    }

    @Override
    protected void startAnimationFadeIn() {
        if (mRecordState) {
            mIvRecordStateAlpha.startAnimation(mAnimationFadeIn);
        }
    }

    @Override
    protected void startAnimationFadeOut() {
        if (mRecordState) {
            mIvRecordStateAlpha.startAnimation(mAnimationFadeOut);
        }
    }

    // 接收websocket的指令
    @Override
    public void onLiveNotify(int type, Object data) {
        switch (type) {
            case LiveNotifyType.sync: {
                int page = (int) data;
                mWsPosition = page;
                setCurrPosition(page);
            }
            break;
            case LiveNotifyType.inquired: {
                switchDevice();
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
            if (mRecordState) {
                mRecordPresenter.onlyStopRecord();
                // 同时删除正在录制的本地音频
                if (mAudioFilePath != null) {
                    boolean b = FileUtil.delFile(new File(mAudioFilePath));
                    YSLog.d(TAG, "无网络后删除正在录制的文件是否成功 = " + b);
                }
            }
            finish();
        }
    }

    @Override
    public void noPermissionState() {
        mIvRecordState.setImageResource(R.drawable.record_ic_can_not_record);
        mIvRecordState.setClickable(false);
        mIvRerecording.setSelected(false);
        mIvRerecording.setClickable(false);
    }

    @Override
    protected void refreshView() {
        changeViewState(getCurrPosition());
    }

    @Override
    public void havePermissionState() {
        initPhoneCallingListener();
    }

    private void videoState() {
        hideView(mTvRemind);
        mTvRecordTime.setText("00:00");
        mIvAudition.setImageResource(R.drawable.record_ic_can_not_audition);
        mIvAudition.setClickable(false);
        mIvRecordState.setImageResource(R.drawable.record_ic_can_not_record);
        mIvRecordState.setClickable(false);
        mIvRerecording.setSelected(false);
        mIvRerecording.setClickable(false);
    }

    // 切换页面,录制,重录的时候如果在播放要停止,修改一些状态
    private void stopPlayOperation() {
        if (mPlayState) {
            mRecordPresenter.stopPlay();
        }
        if (mPlayPause) {
            mPlayPause = false;
        }
        goneView(mLayoutPlay);
    }

    private class View implements RecordContract.V {

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
                // 判断是视频还是图片 如果是图片的话看有没有以前的录制音频
                if (TextUtil.isEmpty(courseDetail.getString(TCourseDetail.videoUrl))) {
                    RecordImgFrag frag = RecordImgFragRouter
                            .create(courseDetail.getString(TCourseDetail.imgUrl))
                            .audioFilePath(CacheUtil.getExistAudioFilePath(mCourseId, courseDetail.getInt(TCourseDetail.id)))
                            .audioUrl(courseDetail.getString(TCourseDetail.audioUrl))
                            .route();
                    add(frag);
                } else {
                    add(RecordVideoFragRouter
                            .create(courseDetail.getString(TCourseDetail.videoUrl), courseDetail.getString(TCourseDetail.imgUrl))
                            .route());
                }
                if (TextUtil.isNotEmpty(courseDetail.getString(TCourseDetail.duration))) {
                    int duration = courseDetail.getInt(TCourseDetail.duration);
                    mRecordTimeArray.put(i, duration);
                } else {
                    mRecordTimeArray.put(i, 0);
                }
            }
            invalidate();
            mAudioUploadPresenter.setCourseDetailIdArray(courseDetailIdArray);

            // 链接websocket
            if (TextUtil.isNotEmpty(wsUrl)) {
                WebSocketServRouter.create(wsUrl).route(RecordActivity.this);
            }
            // 拼接分享参数
            mShareAndStarArg = new Meet();
            mShareAndStarArg.put(TMeet.id, mCourseId);
            Course course = joinMeeting.get(TJoinMeeting.course);
            mShareAndStarArg.put(TMeet.title, course.getString(TCourse.title));
            if (mCourseDetailList != null && mCourseDetailList.size() > 0) {
                mShareAndStarArg.put(TMeet.coverUrl, mCourseDetailList.get(0).getString(TCourseDetail.imgUrl));
            }
            mShareAndStarArg.put(TMeet.playType, CourseType.reb);
            Record record = joinMeeting.get(TJoinMeeting.record);
            if (record == null) {
                mShareAndStarArg.put(TMeet.playState, Record.PlayState.un_start);
            } else {
                mShareAndStarArg.put(TMeet.playState, record.getInt(TRecord.playState));
            }

            mStarState = course.getBoolean(TCourse.starRateFlag);
            mShareAndStarArg.put(TMeet.starRateFlag, mStarState);
            mShareAndStarArg.put(TMeet.password, course.getString(TCourse.password));
            YSLog.d(TAG, "观看密码 = " + course.getString(TCourse.password));

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    // 先判断以前是否有异常退出过，如果异常退出了跳转到异常退出时的页面，否则跳转到以前退出时的页面
                    if (RecordUnusualState.inst().getUnusualExitState()) {
                        String pageId = RecordUnusualState.inst().getString(TRecordUnusualState.pageId);
                        for (int i = 0; i < mCourseDetailList.size(); ++i) {
                            String id = mCourseDetailList.get(i).getString(TCourseDetail.id);
                            if (pageId.equals(id)) {
                                setCurrPosition(i, false);
                                mIvAudition.setImageResource(R.drawable.record_ic_audition);
                                mIvAudition.setClickable(true);
                                mIvRecordState.setImageResource(R.drawable.animation_record);
                                mAnimationRecord = (AnimationDrawable) mIvRecordState.getDrawable();
                                mIvRecordState.setClickable(true);
                                mTvRecordState.setText(R.string.continue_record);
                                mIvRerecording.setClickable(true);
                                mIvRerecording.setSelected(true);
                                mCanContinueRecord = true;
                                break;
                            }
                        }
                        RecordUnusualState.inst().put(TRecordUnusualState.unusualExit, false);
                        RecordUnusualState.inst().saveToSp();
                    } else {
                        Record record = (Record) joinMeeting.getObject(TJoinMeeting.record);
                        if (record != null) {
                            int page = record.getInt(TRecord.playPage);
                            YSLog.d(TAG, "上次退出录音时所在页面 page = " + page);
                            if (page == 0 && mCourseDetailList.size() > 0) {
                                // 判断第一页是视频还是图片
                                if (TextUtil.isNotEmpty(mCourseDetailList.get(0).getString(TCourseDetail.videoUrl))) {
                                    videoState();
                                } else {
                                    // 判断第一页是否已经录制过
                                    if (TextUtil.isEmpty(mCourseDetailList.get(0).getString(TCourseDetail.audioUrl))) {
                                        YSLog.d(TAG, "第一页没有录制过");
                                        showView(mTvRemind);
                                        mIvRecordState.setImageResource(R.drawable.animation_record);
                                        mAnimationRecord = (AnimationDrawable) mIvRecordState.getDrawable();
                                        mIvRecordState.setClickable(true);
                                        mIvAudition.setClickable(false);
                                        mIvRerecording.setSelected(false);
                                        mIvRerecording.setClickable(false);
                                    } else {
                                        YSLog.d(TAG, "第一页已经录制过");
                                        mTvRecordTime.setText(TimeFormatter.second(mRecordTimeArray.get(0), TimeFormat.from_m));
                                        mIvAudition.setImageResource(R.drawable.record_ic_audition);
                                        mIvAudition.setClickable(true);
                                        mIvRecordState.setImageResource(R.drawable.record_ic_can_not_record);
                                        mIvRecordState.setClickable(false);
                                        mIvRerecording.setSelected(true);
                                        mIvRerecording.setClickable(true);
                                    }
                                }
                            } else if (page > 0 && mCourseDetailList.size() > 0) {
                                setCurrPosition(page, false);
                            }
                        }
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
                        showView(mTvLastPageRemind);
                    }
                    removeOnGlobalLayoutListener(this);
                }
            });
        }

        @Override
        public void startRecordState() {
            if (mCanContinueRecord) {
                mContinueRecord = true;
            }
            hideView(mTvRemind);
            mRecordState = true;
            setRecordTime(0);
            mAnimationRecord.selectDrawable(1);
            showView(mIvRecordStateAlpha);
            mIvRecordStateAlpha.startAnimation(mAnimationFadeIn);
            mIvAudition.setImageResource(R.drawable.record_ic_can_not_audition);
            mIvAudition.setClickable(false);
            mIvRerecording.setSelected(false);
            mIvRerecording.setClickable(false);

            mManager.notify(KNotifyId, mBuilder.build());

            getViewPager().setScrollable(false);
            showView(mGestureView);

            RecordUnusualState.inst().put(TRecordUnusualState.page, getCurrPosition());
            RecordUnusualState.inst().put(TRecordUnusualState.pageId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id));
            RecordUnusualState.inst().put(TRecordUnusualState.unusualExit, true);
            RecordUnusualState.inst().saveToSp();
        }

        @Override
        public void setAudioFilePath(String filePath) {
            mAudioFilePath = filePath;
        }

        @Override
        public void setRecordDbLevel(int level) {
            mAnimationRecord.selectDrawable(level);
        }

        @Override
        public void stopRecordState(int pos, int time) {
            // 保存录制的时间
            if (mContinueRecord) {
                time = time + mRecordTimeArray.get(getCurrPosition());
                mRecordTimeArray.put(pos, time);
            } else {
                mRecordTimeArray.put(pos, time);
            }
            mRecordState = false;
            mContinueRecord = false;
            mAnimationRecord.selectDrawable(0);
            mManager.cancel(KNotifyId);

            if (SpApp.inst().showSlideDialog()) {
                getViewPager().setScrollable(false);
                showView(mGestureView);
            } else {
                getViewPager().setScrollable(true);
                goneView(mGestureView);
            }

            mIvRecordStateAlpha.clearAnimation();
            goneView(mIvRecordStateAlpha);

            mIvAudition.setImageResource(R.drawable.record_ic_audition);
            mIvAudition.setClickable(true);
            mIvRerecording.setSelected(true);
            mIvRerecording.setClickable(true);
            // 如果不是续录停止录音的时候上传音频文件 ，续录的话要拼接完音频文件再上传
            if (mCanContinueRecord) {
                mRecordPresenter.jointAudio(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id), getCurrPosition());
            } else {
                uploadAudioFile(mCourseId, getCurrPosition(), CourseType.reb);
            }

            RecordUnusualState.inst().put(TRecordUnusualState.unusualExit, false);
            RecordUnusualState.inst().saveToSp();
        }

        @Override
        public void canNotContinueRecordState() {
            getViewPager().setScrollable(true);
            goneView(mGestureView);
            mCanContinueRecord = false;
            hideView(mTvRemind);
            mTvRecordTime.setText("10:00");
            mRecordTimeArray.put(getCurrPosition(), (int) TimeUnit.MINUTES.toSeconds(10));
            mTvRecordState.setText(R.string.record);
            mIvRecordState.setImageResource(R.drawable.record_ic_can_not_record);
            mIvRecordState.setClickable(false);
        }

        @Override
        public void playState() {
            mPlayState = true;
            mIvAudition.setImageResource(R.drawable.record_ic_audition_ing);
            showView(mLayoutPlay);
            mSeekBar.setProgress(0);
            mTvPlayTime.setText("00:00");
        }

        @Override
        public void setSeekBarMax(int max) {
            mSeekBar.setMax(max);
            mRecordTimeArray.put(getCurrPosition(), (int) (max / TimeUnit.SECONDS.toMillis(1)));
            mTvRecordTime.setText(TimeFormatter.second(mRecordTimeArray.get(getCurrPosition()), TimeFormat.from_m));
        }

        @Override
        public void setSeekBarProgress(int progress) {
            if (!mSeekBarChange) {
                mSeekBar.setProgress(progress);
            }
            mTvPlayTime.setText(TimeFormatter.milli(progress, TimeFormat.from_m));
        }

        @Override
        public void stopPlayState() {
            mPlayState = false;
            mIvAudition.setImageResource(R.drawable.record_ic_audition);
        }

        @Override
        public void setRecordTime(int time) {
            if (mContinueRecord) {
                time = time + mRecordTimeArray.get(getCurrPosition());
                mTvRecordTime.setText(TimeFormatter.second(time, TimeFormat.from_m));
            } else {
                mTvRecordTime.setText(TimeFormatter.second(time, TimeFormat.from_m));
            }
        }

        @Override
        public void setRecordTimeRemind(int minute) {
            showView(mTvRemind);
            mTvRemind.setTextColor(ResLoader.getColor(R.color.text_ace400));
            mTvRemind.setText(String.format(getString(R.string.record_time_insufficient_remind), minute));
            // 设置震动周期，数组表示时间：等待+执行，单位是毫秒，下面操作代表:等待100，执行200，等待100，执行500，
            // 后面的数字如果为-1代表不重复，只执行一次，其他代表会重复，0代表从数组的第0个位置开始
            mVibrator.vibrate(new long[]{100, 150, 100, 500}, -1);
        }

        @Override
        public void uploadJointAudio(String filePath, int pos) {
            uploadAudioFile(mCourseId, pos, CourseType.reb, filePath, 0);
        }

        @Override
        public void showToast(int id) {
            if (mRecordState) {
                mRecordState = false;
                mAnimationRecord.selectDrawable(0);
                getViewPager().setScrollable(true);
                mIvRecordStateAlpha.clearAnimation();
                goneView(mIvRecordStateAlpha);
                goneView(mGestureView);
            }
            if (mPlayState) {
                mRecordPresenter.stopPlay();
                goneView(mLayoutPlay);
            }
            RecordActivity.this.showToast(id);
        }

        @Override
        public void finishRecord() {
            finish();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {
            RecordActivity.this.setViewState(state);
        }
    }
}
