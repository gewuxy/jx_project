package jx.csp.ui.activity.record;

import android.app.Service;
import android.support.annotation.CallSuper;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import inject.annotation.router.Arg;
import jx.csp.R;
import jx.csp.contact.AudioUploadContract;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.WebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.TWebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.WsOrderFrom;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.presenter.AudioUploadPresenterImpl;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.activity.record.RecordActivity.OverType;
import jx.csp.util.CacheUtil;
import jx.csp.util.ScaleTransformer;
import jx.csp.util.Util;
import jx.csp.view.GestureView;
import jx.csp.view.VoiceLineView;
import lib.jx.notify.LiveNotifier;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.notify.LiveNotifier.OnLiveNotify;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseVpActivity;
import lib.ys.YSLog;
import lib.ys.receiver.ConnectionReceiver;
import lib.ys.receiver.ConnectionReceiver.OnConnectListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;

/**
 * 录音页面
 *
 * @author CaiXiang
 * @since 2017/9/30
 */

abstract public class BaseRecordActivity extends BaseVpActivity implements
        OnLiveNotify,
        OnConnectListener,
        AudioUploadContract.V {

    protected final int KMicroPermissionCode = 10;
    protected final int KOne = 1;
    private final int KVpSize = 3; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长
    private final float KVpScale = 0.044f; // vp的缩放比例

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

    protected List<CourseDetail> mCourseDetailList;
    protected AudioUploadPresenterImpl mAudioUploadPresenter;
    protected PhoneStateListener mPhoneStateListener = null;  // 电话状态监听
    private ConnectionReceiver mConnectionReceiver;
    private ScaleTransformer mTransformer;

    @Arg
    String mCourseId;  // 课程id
    @Arg
    String mCoverUrl;  // 分享封面地址
    @Arg
    String mTitle;  // 会议标题

    @CallSuper
    @Override
    public void initData() {
        // 禁止手机锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //创建文件夹存放音频
        FileUtil.ensureFileExist(CacheUtil.getAudioCacheDir() + File.separator + mCourseId);
        mConnectionReceiver = new ConnectionReceiver(this);
        mConnectionReceiver.setListener(this);
        mAudioUploadPresenter = new AudioUploadPresenterImpl(this);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            ShareDialog dialog = new ShareDialog(this, mCourseId, mTitle, mCoverUrl);
            dialog.show();
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_base_record;
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

        setOnClickListener(R.id.record_iv_last);
        setOnClickListener(R.id.record_iv_next);

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText(String.valueOf(getCurrPosition() + KOne));
                pageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mTransformer = new ScaleTransformer(KVpScale, Util.calcVpOffset(getViewPager().getPaddingLeft(), getViewPager().getWidth()));
                setPageTransformer(false, mTransformer);
                removeOnGlobalLayoutListener(this);
            }
        });
        LiveNotifier.inst().add(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mConnectionReceiver.register();
    }

    @Override
    public void onClick(View v) {
        if (getCount() == 0) {
            return;
        }
        switch (v.getId()) {
            case R.id.record_iv_last: {
                if (getCurrPosition() < KOne) {
                    showToast(R.string.first_page);
                    return;
                }
                skipToLast();
            }
            break;
            case R.id.record_iv_next: {
                if (getCurrPosition() >= (mCourseDetailList.size() - KOne)) {
                    showToast(R.string.last_page);
                    return;
                }
                skipToNext();
            }
            break;
            default:
                onClick(v.getId());
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 注销服务
        if (mConnectionReceiver != null) {
            mConnectionReceiver.unRegister();
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销电话监听
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        mPhoneStateListener = null;
        LiveNotifier.inst().remove(this);
        WebSocketServRouter.stop(this);
        YSLog.d(TAG, "base record activity WebSocketServRouter.stop");
        int overType = OverType.no;
        if (mCourseDetailList != null && getCurrPosition() == (mCourseDetailList.size() - KOne)) {
            overType = OverType.over;
        }
        CommonServRouter.create(ReqType.exit_record)
                .courseId(mCourseId)
                .pageNum(getCurrPosition())
                .overType(overType)
                .route(this);
    }

    @Override
    public void onStopRefresh() {
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.delete_meeting_success) {
            finish();
        }
    }

    protected void setNavBarMidText(String str) {
        if (mTvNavBar == null) {
            mTvNavBar = getNavBar().addTextViewMid(str);
        } else {
            mTvNavBar.setText(str);
        }
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
                        onCallOffHooK();
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 上传音频文件
     *
     * @param courseId
     * @param page
     * @param type
     */
    protected void uploadAudioFile(String courseId, int page, @PlayType int type) {
        String audioFilePath = CacheUtil.getAudioPath(courseId, page);
        uploadAudioFile(courseId, page, type, audioFilePath, 0);
    }

    protected void uploadAudioFile(String courseId, int page, @PlayType int type, String audioFilePath, int time) {
        mAudioUploadPresenter.uploadAudioFile(courseId, page, type, audioFilePath, time);
    }

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

    abstract protected void onClick(int id);

    abstract protected void pageSelected(int position);

    abstract protected void skipToLast();

    abstract protected void skipToNext();

    abstract protected void onCallOffHooK();

    abstract protected void switchDevice();
}
