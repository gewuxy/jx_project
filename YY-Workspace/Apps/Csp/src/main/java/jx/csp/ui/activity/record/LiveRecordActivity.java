package jx.csp.ui.activity.record;

import java.util.ArrayList;

import jx.csp.R;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Course.TCourse;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import jx.csp.model.meeting.Live;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.record.LiveRecordContract.LiveRecordView;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import lib.zego.ZegoApiManager;

/**
 * 直播录制
 *
 * @author CaiXiang
 * @since 2017/10/10
 */

public class LiveRecordActivity extends BaseRecordActivity implements LiveRecordView {

    private final int KSixty = 60;

    private LiveRecordPresenterImpl mLiveRecordPresenterImpl;
    private boolean mBeginCountDown = false;  // 是否开始倒计时,直播时间到了才开始
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mStopCountDown = false; // 是否开始进行结束倒计时
    private int mLastPage = 0; // 上一页的位置

    private String mTitle = "直播语音 9月30日 14：00";
    private long mStartTime = System.currentTimeMillis() - 10 * 60 * 1000;
    private long mStopTime = System.currentTimeMillis() + 35 * 60 * 1000;
    private String mCourseId;  // 课程id

    private Live mLiveMsg;

    @Override
    public void initData() {
        ZegoApiManager.getInstance().init(this, "666", "人数获取测试");
        mLiveRecordPresenterImpl = new LiveRecordPresenterImpl(this);
        mCourseId = "14379";
        //创建文件夹存放音频
        FileUtil.ensureFileExist(CacheUtil.getAudioCacheDir() + "/" + mCourseId);
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
            mTvNavBar.setText(mTitle);
            mTvStartRemain.setText(R.string.meeting_no_start_remain);
        }

        //请求网络
        exeNetworkReq(KJoinMeetingReqId, MeetingAPI.join(mCourseId).build());
    }

    @Override
    protected void onClick(int id) {
        if (Util.noNetwork()) {
            return;
        }
        if (id == R.id.record_iv_state) {
            String filePath = CacheUtil.getAudioCacheDir() + "/" + mCourseId + "/" + (getCurrentItem() + 1) + KAudioSuffix;
            // 判断直播时间是否已经到了
            if (mBeginCountDown) {
                if (mLiveState) {
                    // 如果当前页是视频页面，则不调stopLiveRecord()这个方法,显示停止状态
                    // 如果停止的时候是在音频页面要上传音频
                    if (getItem(getCurrentItem()) instanceof RecordImgFrag && ((RecordImgFrag) getItem(getCurrentItem())).getFragType() == FragType.img) {
                        mLiveRecordPresenterImpl.stopLiveRecord();
                        uploadAudioFile(mCourseId, getCurrentItem(), PlayType.live);
                    } else {
                        stopRecordState();
                    }
                } else {
                    // 如果点击开始直播是在视频页，不需要录音，在直播状态
                    if (getItem(getCurrentItem()) instanceof RecordImgFrag && ((RecordImgFrag) getItem(getCurrentItem())).getFragType() == FragType.img) {
                        mLiveRecordPresenterImpl.startLiveRecord(filePath);
                    } else {
                        startRecordState();
                    }
                }
            } else {
                startCountDownAndLive(filePath);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        YSLog.d(TAG, "onPageSelected position = " + position);
        // 在直播的时候翻页要先停止录音然后上传音频文件，再重新开始录音
        if (mLiveState) {
            // 在直播的时候翻页,如果上一页是视频，则不掉stopLiveRecord()这个方法
            if (getItem(mLastPage) instanceof RecordImgFrag && ((RecordImgFrag) getItem(mLastPage)).getFragType() == FragType.img) {
                mLiveRecordPresenterImpl.stopLiveRecord();
                // 上传上一页的音频 确保存在
                uploadAudioFile(mCourseId, mLastPage, PlayType.live);
            }
            // 如果下一页是视频则不要录音，但页面状态是显示在直播状态，视频页滑到其他页不要上传音频
            if (getItem(position) instanceof RecordImgFrag && ((RecordImgFrag) getItem(position)).getFragType() == FragType.img) {
                String filePath = CacheUtil.getAudioCacheDir() + "/" + mCourseId + "/" + (position + 1) + KAudioSuffix;
                mLiveRecordPresenterImpl.startLiveRecord(filePath);
            } else {
                startRecordState();
            }
            // 同步指令
            exeNetworkReq(KSyncReqId, MeetingAPI.sync()
                    .courseId(mCourseId)
                    .pageNum(position)
                    .build());
        }
        // 记录位置
        mLastPage = position;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveRecordPresenterImpl.onDestroy();
        ZegoApiManager.getInstance().releaseSDK();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KJoinMeetingReqId) {
            return JsonParser.ev(r.getText(), JoinMeeting.class);
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KJoinMeetingReqId) {
            Result<JoinMeeting> r = (Result<JoinMeeting>) result;
            if (r.isSucceed()) {
                mJoinMeeting = r.getData();
                mWebSocketUrl = mJoinMeeting.getString(TJoinMeeting.wsUrl);
                mCourseDetailList = (ArrayList<CourseDetail>) mJoinMeeting.get(TJoinMeeting.course).getList(TCourse.details);
                mTvTotalPage.setText(String.valueOf(mCourseDetailList.size()));
                for (int i = 0; i < mCourseDetailList.size(); ++i) {
                    CourseDetail courseDetail = mCourseDetailList.get(i);
                    // 判断是视频还是图片
                    if (TextUtil.isEmpty(courseDetail.getString(TCourseDetail.videoUrl))) {
                        add(RecordImgFragRouter
                                .create()
                                .url(courseDetail.getString(TCourseDetail.imgUrl))
                                .route());
                    } else {
                        add(RecordVideoFragRouter
                                .create()
                                .videoUrl(courseDetail.getString(TCourseDetail.videoUrl))
                                .route());
                    }
                }
                invalidate();
                // 链接websocket
                if (TextUtil.isNotEmpty(mWebSocketUrl)) {
                    mWebSocket = exeWebSocketReq(NetworkReq.newBuilder(mWebSocketUrl).build(), new WebSocketLink());
                }
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    @Override
    protected void onCallOffHooK() {
        if (mLiveState) {
            // 判断当前页是不是视频
            if (getItem(getCurrentItem()) instanceof RecordImgFrag && ((RecordImgFrag) getItem(getCurrentItem())).getFragType() == FragType.img) {
                mLiveRecordPresenterImpl.stopLiveRecord();
            } else {
                stopRecordState();
            }
        }
    }

    @Override
    public void setLiveTimeTv(String str) {
        mTvNavBar.setText(str);
    }

    @Override
    public void startRecordState() {
        goneView(mTvStartRemain);
        mLiveState = true;
        mIvRecordState.setSelected(true);
        if (mStopCountDown) {
            mTvRecordState.setText(R.string.record_live_stop);
        } else {
            mTvRecordState.setText(R.string.record_live_ing);
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
    public void setOnlineTv(String str) {
        mTvOnlineNum.setText(str);
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
    public void changeRecordIvRes() {
        mStopCountDown = true;
        mIvRecordState.setImageResource(R.drawable.selector_record_live_state_warm);
        mTvRecordState.setTextColor(ResLoader.getColor(R.color.text_d0011b));
        mTvNavBar.setTextColor(ResLoader.getColor(R.color.text_d0011b));
        if (mLiveState) {
            mTvRecordState.setText(R.string.record_live_stop);
        }
    }

    @Override
    public void showToast(int id) {
        stopRecordState();
        super.showToast(id);
    }

    @Override
    public void onFinish() {
        finish();
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

    protected void havePermissionState() {
        initPhoneCallingListener();
        goneView(mTvStartRemain);
        mIvRecordState.setClickable(true);
        mIvVoiceState.setSelected(true);
    }

    protected void noPermissionState() {
        showView(mTvStartRemain);
        mTvStartRemain.setText(R.string.no_record_permission);
        mIvRecordState.setClickable(false);
        mIvVoiceState.setSelected(false);
    }

    protected void startCountDownAndLive(String filePath) {
        if (System.currentTimeMillis() >= mStartTime) {
            mBeginCountDown = true;
            mLiveRecordPresenterImpl.startCountDown(mStartTime, mStopTime);
            // 如果点击开始直播是在视频页，不需要录音，在直播状态
            if (getItem(getCurrentItem()) instanceof RecordImgFrag && ((RecordImgFrag) getItem(getCurrentItem())).getFragType() == FragType.img) {
                mLiveRecordPresenterImpl.startLiveRecord(filePath);
            } else {
                startRecordState();
            }
        } else {
            showToast(R.string.live_time_no_arrived);
        }
    }

}