package jx.csp.ui.activity.record;

import java.util.ArrayList;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.LiveRecordContract;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Course.TCourse;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import jx.csp.model.meeting.Live;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.presenter.LiveRecordPresenterImpl;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.ui.frag.record.RecordImgFragRouter;
import jx.csp.ui.frag.record.RecordVideoFragRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;

/**
 * 直播录制
 *
 * @author CaiXiang
 * @since 2017/10/10
 */
@Route
public class LiveRecordActivity extends BaseRecordActivity {

    private final int KSixty = 60;

    private LiveRecordPresenterImpl mLiveRecordPresenterImpl;
    private boolean mBeginCountDown = false;  // 是否开始倒计时,直播时间到了才开始
    private boolean mLiveState = false;  // 直播状态  true 直播中 false 未开始
    private boolean mStopCountDown = false; // 是否开始进行结束倒计时
    private int mLastPage = 0; // 上一页的位置

    private String mTitle = "直播语音 9月30日 14：00";
    private long mStartTime = System.currentTimeMillis() - 10 * 60 * 1000;
    private long mStopTime = System.currentTimeMillis() + 35 * 60 * 1000;

    private View mView;
    private Live mLiveMsg;

    @Override
    public void initData() {
        super.initData();

        mView = new View();
        mLiveRecordPresenterImpl = new LiveRecordPresenterImpl(mView);
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
        exeNetworkReq(KJoinMeetingReqId, MeetingAPI.join(mCourseId).build());
    }

    @Override
    protected void onClick(int id) {
        if (Util.noNetwork()) {
            return;
        }
        if (id == R.id.record_iv_state) {
            String filePath = CacheUtil.getAudioPath(mCourseId, getCurrentItem());
            // 判断直播时间是否已经到了
            if (mBeginCountDown) {
                if (mLiveState) {
                    // 如果当前页是视频页面，则不调stopLiveRecord()这个方法,显示停止状态
                    // 如果停止的时候是在音频页面要上传音频
                    if (getItem(getCurrentItem()) instanceof RecordImgFrag && ((RecordImgFrag) getItem(getCurrentItem())).getFragType() == FragType.img) {
                        mLiveRecordPresenterImpl.stopLiveRecord();
                        uploadAudioFile(mCourseId, getCurrentItem(), PlayType.live);
                    } else {
                        mView.stopRecordState();
                    }
                } else {
                    // 如果点击开始直播是在视频页，不需要录音，在直播状态
                    if (getItem(getCurrentItem()) instanceof RecordImgFrag && ((RecordImgFrag) getItem(getCurrentItem())).getFragType() == FragType.img) {
                        mLiveRecordPresenterImpl.startLiveRecord(filePath);
                    } else {
                        mView.startRecordState();
                    }
                }
            } else {
                startCountDownAndLive(filePath);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLiveState) {
            mLiveRecordPresenterImpl.stopLiveRecord();
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
                String filePath = CacheUtil.getAudioPath(mCourseId, position);
                mLiveRecordPresenterImpl.startLiveRecord(filePath);
            } else {
                mView.startRecordState();
            }
            webSocketSendMsg(position);
        }
        // 记录位置
        mLastPage = position;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveRecordPresenterImpl.onDestroy();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KJoinMeetingReqId) {
            return JsonParser.ev(r.getText(), JoinMeeting.class);
        } else {
            return JsonParser.error(r.getText());
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
                                .create(courseDetail.getString(TCourseDetail.imgUrl))
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
                mView.stopRecordState();
            }
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
            if (getItem(getCurrentItem()) instanceof RecordImgFrag && ((RecordImgFrag) getItem(getCurrentItem())).getFragType() == FragType.img) {
                mLiveRecordPresenterImpl.startLiveRecord(filePath);
            } else {
                mView.startRecordState();
            }
        } else {
            showToast(R.string.meeting_no_start_remain);
        }
    }

    private class View implements LiveRecordContract.View {

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
            LiveRecordActivity.this.showToast(id);
        }

        @Override
        public void onFinish() {
            finish();
        }
    }

}
