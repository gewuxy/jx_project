package jx.csp.ui.activity.record;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.LiveRecordContract;
import jx.csp.dialog.BigButtonDialog;
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
import jx.csp.presenter.LiveRecordPresenterImpl;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.ui.frag.record.RecordImgFragRouter;
import jx.csp.ui.frag.record.RecordVideoFragRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.receiver.ConnectionReceiver.TConnType;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * 直播录制
 *
 * @author CaiXiang
 * @since 2017/10/10
 */
@Route
public class LiveRecordActivity extends BaseRecordActivity {

    private final int KSixty = (int) TimeUnit.MINUTES.toSeconds(1);

    private LiveRecordPresenterImpl mLiveRecordPresenterImpl;
    private boolean mBeginCountDown = false;  // 是否开始倒计时,直播时间到了才开始
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mStopCountDown = false; // 是否开始进行结束倒计时
    private int mLastPage = 0; // 上一页的位置

    @Arg(opt = true)
    long mStartTime;
    @Arg(opt = true)
    long mStopTime;
    long mRealStopTime;

    private View mView;
    private String mFilePath; // 正在录制的音频文件名字

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mRealStopTime = mStopTime + TimeUnit.MINUTES.toMillis(15);
        mView = new View();
        mLiveRecordPresenterImpl = new LiveRecordPresenterImpl(mView, mCourseId);
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
        setOnClickListener(R.id.record_iv_state);
        // 判断直播是否已经开始
        if (System.currentTimeMillis() >= mStartTime) {
            mBeginCountDown = true;
            mLiveRecordPresenterImpl.startCountDown(mStartTime, mStopTime);
            mTvStartRemain.setText(R.string.meeting_start_click_start_live);
        } else {
            setNavBarMidText(mTitle);
            mTvStartRemain.setText(R.string.meeting_no_start_remain);
        }

        //请求网络
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
                    }
                } else {
                    // 如果点击开始直播是在视频页，不需要录音，在直播状态
                    if (f instanceof RecordImgFrag) {
                        mLiveRecordPresenterImpl.startLiveRecord(filePath);
                    } else {
                        mView.startRecordState();
                    }
                    // 点击开始直播的时候要发同步指令
                    notifyServ(LiveNotifyType.send_msg, getCurrPosition(), WsOrderType.sync);
                    YSLog.d(TAG, "点击开始直播,发同步指令 pos = " + getCurrPosition());
                }
            } else {
                startCountDownAndLive(filePath);
            }
        }
    }

    @Override
    protected void skipToLast() {
        setCurrentItem(getCurrPosition() - KOne);
    }

    @Override
    protected void skipToNext() {
        setCurrentItem(getCurrPosition() + KOne);
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
            // 如果上一页是的录音页面， 录音时间小于3秒 不发同步指令  在视频页面要发同步指令
            // 在直播的时候翻页,如果上一页是视频，则不掉stopLiveRecord()这个方法 要告诉服务器是翻的视频页
            Fragment f1 = getItem(mLastPage);
            if (f1 instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                if (mFilePath != null && (new File(mFilePath)).exists()) {
                    byte[] bytes = FileUtil.fileToBytes(mFilePath);
                    // 音频小于三秒的不发同步指令 1s差不多1500 byte
                    if (bytes.length < 4500) {
                        YSLog.d(TAG, "翻页时时间间隔小于3秒，不发同步指令");
                    } else {
                        if (position != mWsPosition) {
                            YSLog.d(TAG, "上一页是录音  发同步指令 pos = " + position);
                            notifyServ(LiveNotifyType.send_msg, position, WsOrderType.sync);
                        }
                    }
                }
                // 上传上一页的音频 确保存在
                uploadAudioFile(mCourseId, mLastPage, PlayType.live, mFilePath);
            } else {
                YSLog.d(TAG, "上一页是视频  发同步指令 pos = " + position);
                notifyServ(LiveNotifyType.send_msg, position, WsOrderType.sync);
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
        YSLog.d(TAG, "是否切换直播设备");
        BigButtonDialog dialog = new BigButtonDialog(this);
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
                mWsPosition = page;
                setCurrentItem(page);
            }
            break;
            case LiveNotifyType.inquired: {
                switchDevice();
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
                // 接收到流量不足警告
                CommonDialog2 dialog = new CommonDialog2(this);
                dialog.setHint(R.string.record_live_insufficient);
                dialog.addBlueButton(R.string.ok);
                dialog.show();
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
        if (System.currentTimeMillis() >= mStartTime) {
            mBeginCountDown = true;
            mLiveRecordPresenterImpl.startCountDown(mStartTime, mStopTime);
            // 如果点击开始直播是在视频页，不需要录音，在直播状态
            if (getItem(getCurrPosition()) instanceof RecordImgFrag) {
                mLiveRecordPresenterImpl.startLiveRecord(filePath);
            } else {
                mView.startRecordState();
            }
        } else {
            showToast(R.string.meeting_no_start_remain);
        }
    }

    private class View implements LiveRecordContract.V {

        @Override
        public void setData(JoinMeeting joinMeeting) {
            String wsUrl = joinMeeting.getString(TJoinMeeting.wsUrl);
            mCourseDetailList = (ArrayList<CourseDetail>) joinMeeting.get(TJoinMeeting.course).getList(TCourse.details);
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
                        setCurrentItem(page);
                    }

                    removeOnGlobalLayoutListener(this);
                }
            });
            invalidate();
            // 链接websocket
            if (TextUtil.isNotEmpty(wsUrl)) {
                WebSocketServRouter.create(wsUrl).route(LiveRecordActivity.this);
            }
        }

        @Override
        public void setLiveTimeTv(String str) {
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
        public void setCountDownRemainTv(boolean show, long l) {
            if (show) {
                showView(mTvTimeRemain);
            }
            if (l >= KSixty) {
                mTvTimeRemain.setText(String.format(getString(R.string.live_stop_remind_minute), l / KSixty));
            } else {
                mTvTimeRemain.setText(String.format(getString(R.string.live_stop_remind_second), l));
            }
        }

        @Override
        public void upload(int type, String audioFilePath) {
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
            LiveRecordActivity.this.showToast(id);
        }

        @Override
        public void onFinish() {
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
