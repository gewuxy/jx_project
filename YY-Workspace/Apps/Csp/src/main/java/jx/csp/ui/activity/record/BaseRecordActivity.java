package jx.csp.ui.activity.record;

import android.app.Service;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.LinkedList;

import inject.annotation.router.Arg;
import jx.csp.R;
import jx.csp.contact.VPEffectContract;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.WebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.TWebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.WsOrderFrom;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.presenter.VPEffectPresenterImpl;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import jx.csp.view.GestureView;
import jx.csp.view.VoiceLineView;
import lib.network.model.NetworkReq;
import lib.ys.YSLog;
import lib.ys.receiver.ConnectionReceiver;
import lib.ys.receiver.ConnectionReceiver.OnConnectListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.yy.notify.LiveNotifier;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.notify.LiveNotifier.OnLiveNotify;
import lib.yy.ui.activity.base.BaseVpActivity;

/**
 * 录音页面
 *
 * @author CaiXiang
 * @since 2017/9/30
 */

abstract public class BaseRecordActivity extends BaseVpActivity implements OnLiveNotify, VPEffectContract.V, OnConnectListener {

    protected final int KMicroPermissionCode = 10;
    protected final int KJoinMeetingReqId = 20;
    protected final int KUploadAudioReqId = 30;
    protected final int KUploadVideoPage = 40;  // 视频页翻页时调用的
    protected final int KOne = 1;
    private final int KVpSize = 3; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长
    private final float KVpScale = 0.11f; // vp的缩放比例

    protected TextView mTvNavBar;
    protected TextView mTvTimeRemain;
    protected TextView mTvCurrentPage;
    protected TextView mTvTotalPage;
    protected TextView mTvOnlineNum;
    protected TextView mTvRecordState;
    protected TextView mTvStartRemain;

    protected ImageView mIvVoiceState;
    protected ImageView mIvRecordState;
    protected VoiceLineView mVoiceLine;
    protected View mLayoutOnline;
    protected GestureView mGestureView;

    private VPEffectContract.P mEffectPresenter;
    protected PhoneStateListener mPhoneStateListener = null;  // 电话状态监听

    protected JoinMeeting mJoinMeeting; // 全部数据
    protected ArrayList<CourseDetail> mCourseDetailList;

    private LinkedList<NetworkReq> mUploadList;  // 上传音频队列
    private LinkedList<String> mUploadFilePathList; // 直播时上传音频地址列表，上传完删除
    private boolean mUploadState = false; // 是否在上传音频
    protected int mWsPosition = 0;  // websocket接收到的页数
    protected WebSocketServRouter mWebSocketServRouter;
    private ConnectionReceiver mConnectionReceiver;

    @Arg
    String mCourseId;  // 课程id
    String mTitle;

    @Override
    public void onStopRefresh() {
    }

    @IntDef({
            FragType.img,
            FragType.video,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FragType {
        int img = 1;  // 图片
        int video = 2;  // 视频
    }

    @CallSuper
    @Override
    public void initData() {
        // 禁止手机锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //创建文件夹存放音频
        FileUtil.ensureFileExist(CacheUtil.getAudioCacheDir() + File.separator + mCourseId);
        LiveNotifier.inst().add(this);
        mEffectPresenter = new VPEffectPresenterImpl(this, KVpScale);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            ShareDialog dialog = new ShareDialog(this, mTitle, Integer.valueOf(mCourseId));
            dialog.setDeleteListener(()->{

            });
            dialog.show();
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_record;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTimeRemain = findView(R.id.record_tv_time_remain);
        mTvCurrentPage = findView(R.id.record_tv_current_page);
        mTvTotalPage = findView(R.id.record_tv_total_page);
        mTvOnlineNum = findView(R.id.record_tv_online_num);
        mTvRecordState = findView(R.id.record_tv_state);
        mTvStartRemain = findView(R.id.record_tv_start_remain);
        mIvVoiceState = findView(R.id.record_iv_voice_state);
        mIvRecordState = findView(R.id.record_iv_state);
        mVoiceLine = findView(R.id.record_voice_line);
        mLayoutOnline = findView(R.id.record_online_layout);
        mGestureView = findView(R.id.gesture_view);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);
        getViewPager().setPageMargin(fitDp(27));
        setOnClickListener(R.id.record_iv_last);
        setOnClickListener(R.id.record_iv_next);
        mUploadList = new LinkedList<>();
        mUploadFilePathList = new LinkedList<>();
        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mEffectPresenter.onPageScrolled(getPagerAdapter(), position, positionOffset, getCount());
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText(String.valueOf(getCurrentItem() + KOne));
                pageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mConnectionReceiver = new ConnectionReceiver(this);
        mConnectionReceiver.setListener(this);
        mConnectionReceiver.register();
    }

    @Override
    public void onClick(View v) {
        if (getCount() == 0) {
            return;
        }
        switch (v.getId()) {
            case R.id.record_iv_last: {
                if (getCurrentItem() < KOne) {
                    showToast(R.string.first_page);
                    return;
                }
                setCurrentItem(getCurrentItem() - KOne);
            }
            break;
            case R.id.record_iv_next: {
                if (getCurrentItem() >= (mCourseDetailList.size() - KOne)) {
                    showToast(R.string.last_page);
                    return;
                }
                setCurrentItem(getCurrentItem() + KOne);
            }
            break;
            default:
                onClick(v.getId());
                break;
        }
    }

    abstract protected void onClick(int id);

    @Override
    protected void onPause() {
        super.onPause();

        // 注销服务
        mConnectionReceiver.unRegister();
    }

    protected void setNavBarMidText(String str) {
        if (mTvNavBar == null) {
            mTvNavBar = getNavBar().addTextViewMid(str);
        } else {
            mTvNavBar.setText(str);
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销电话监听
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mPhoneStateListener = null;
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        LiveNotifier.inst().remove(this);
        if (mWebSocketServRouter != null) {
            mWebSocketServRouter.stop(this);
            YSLog.d(TAG, "base record activity WebSocketServRouter.stop");
        }
    }

    @CallSuper
    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KUploadAudioReqId) {
            YSLog.d(TAG, "移除任务");
            mUploadList.removeFirst();
            if (mUploadFilePathList != null && mUploadFilePathList.size() > 0) {
                boolean b = FileUtil.delFile(new File(mUploadFilePathList.getFirst()));
                YSLog.d(TAG, "直播音频文件删除成功？ = " + b);
                mUploadFilePathList.removeFirst();
            }
            mUploadState = false;
            upload();
        }
    }

    abstract protected void pageSelected(int position);

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
                        onCallOffHooK();
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    abstract protected void onCallOffHooK();

    /**
     * 上传音频文件
     *
     * @param courseId
     * @param page
     * @param type
     */
    protected void uploadAudioFile(String courseId, int page, @PlayType int type) {
        String audioFilePath = CacheUtil.getAudioPath(courseId, page);
        uploadAudioFile(courseId, page, type, audioFilePath);
    }

    protected void uploadAudioFile(String courseId, int page, @PlayType int type, String audioFilePath) {
        File file = new File(audioFilePath);
        if (file.exists()) {
            byte[] bytes = FileUtil.fileToBytes(audioFilePath);
            YSLog.d(TAG, "upload audioFilePath = " + audioFilePath);
            YSLog.d(TAG, "bytes = " + bytes.length);
            // 直播时小于三秒的音频不上传并且删除文件 1s差不多1500 byte
            if (type == PlayType.live && bytes.length < 4500) {
                YSLog.d(TAG, "直播时小于三秒的音频不上传");
                FileUtil.delFile(file);
                return;
            }
            NetworkReq req = MeetingAPI.uploadAudio()
                    .courseId(courseId)
                    .detailId(mCourseDetailList.get(page).getString(TCourseDetail.id))
                    .pageNum(page)
                    .playType(type)
                    .file(bytes)
                    .build();
            mUploadList.addLast(req);
            if (type == PlayType.live || type == PlayType.video) {
                mUploadFilePathList.addLast(audioFilePath);
            }
            upload();
        }
    }

    private void upload() {
        if (mUploadList.isEmpty()) {
            YSLog.d(TAG, "上传列表为空");
            return;
        }
        if (!mUploadState) {
            YSLog.d(TAG, "开始上传任务");
            exeNetworkReq(KUploadAudioReqId, mUploadList.getFirst());
            mUploadState = true;
        }
    }

    abstract protected void switchDevice();

    protected void notifyServ(@LiveNotifyType int type, @WsOrderType int orderType) {
        notifyServ(type, 0, orderType);
    }

    /**
     * 发送通知
     *
     * @param type
     * @param position
     * @param orderType
     */
    protected void notifyServ(@LiveNotifyType int type, int position, @WsOrderType int orderType) {
        WebSocketMsg msg = new WebSocketMsg();
        msg.put(TWebSocketMsg.courseId, mCourseId);
        msg.put(TWebSocketMsg.order, orderType);
        msg.put(TWebSocketMsg.orderFrom, WsOrderFrom.app);
        msg.put(TWebSocketMsg.pageNum, position);
        msg.put(TWebSocketMsg.detailId, mCourseDetailList.get(position).getString(TCourseDetail.id));
        msg.put(TWebSocketMsg.imgUrl, mCourseDetailList.get(position).getString(TCourseDetail.imgUrl));
        msg.put(TWebSocketMsg.videoUrl, mCourseDetailList.get(position).getString(TCourseDetail.videoUrl));
        LiveNotifier.inst().notify(type, msg.toJson());
    }

}
