package jx.csp.ui.activity.record;

import android.app.Service;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import jx.csp.R;
import jx.csp.model.meeting.Course;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import jx.csp.view.GestureView;
import jx.csp.view.VoiceLineView;
import lib.network.model.NetworkReq;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.yy.ui.activity.base.BaseVPActivity;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * 录音页面
 *
 * @author CaiXiang
 * @since 2017/9/30
 */

abstract public class BaseRecordActivity extends BaseVPActivity implements OnPageChangeListener {

    protected static final int KWebSocketCloseNormal = 1000; //  1000表示正常关闭
    protected final int KMicroPermissionCode = 10;
    protected final int KJoinMeetingReqId = 10;
    protected final int KSyncReqId = 20;
    protected final int KUploadAudioReqId = 30;
    protected final String KAudioSuffix = ".amr";
    private final int KOne = 1;
    private final int KVpSize = 2; // Vp缓存的数量
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

    private float mLastOffset;
    protected PhoneStateListener mPhoneStateListener = null;  // 电话状态监听

    protected JoinMeeting mJoinMeeting; // 全部数据
    protected Course mCourseMsg;
    protected ArrayList<CourseDetail> mCourseDetailList;

    protected WebSocket mWebSocket;
    protected boolean mWsSuccess = false; // WebSocket连接是否成功
    protected String mWebSocketUrl; // WebSocket地址

    @IntDef({
            FragType.img,
            FragType.video,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface FragType {
        int img = 1;  // 图片
        int video = 2;  // 视频
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        mTvNavBar = bar.addTextViewMid(" ");
        bar.addViewRight(R.drawable.share_ic_share, v -> showToast("share"));
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
        setOnPageChangeListener(this);
        setOnClickListener(R.id.record_iv_last);
        setOnClickListener(R.id.record_iv_next);
    }

    @Override
    public void onClick(View v) {
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
    protected void onDestroy() {
        super.onDestroy();
        // 注销电话监听
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mPhoneStateListener = null;
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        if (mWebSocket != null) {
            mWebSocket.close(KWebSocketCloseNormal, "close");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition;
        float realOffset;
        int nextPosition;
        if (mLastOffset > positionOffset) {
            realPosition = position + KOne;
            nextPosition = position;
            realOffset = KOne - positionOffset;
        } else {
            realPosition = position;
            nextPosition = position + KOne;
            realOffset = positionOffset;
        }

        if (nextPosition > getCount() - KOne || realPosition > getCount() - KOne) {
            return;
        }

        viewChange(realPosition, KOne - realOffset);
        viewChange(nextPosition, realOffset);

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
        mTvCurrentPage.setText(String.valueOf(getCurrentItem() + KOne));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
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

    abstract protected void onCallOffHooK();

    /**
     * 改变view的大小  缩放
     */
    private void viewChange(int position, float offset) {
        View view = getItem(position).getView();
        if (view == null) {
            return;
        }
        float scale = KOne + KVpScale * offset;
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    /**
     * 上传音频文件
     * @param courseId
     * @param page
     * @param type
     */
    protected void uploadAudioFile(String courseId, int page, int type) {
        String audioFilePath = CacheUtil.getAudioCacheDir() + "/" + courseId + "/" + (page + 1) + KAudioSuffix;
        File file = new File(audioFilePath);
        if (file.exists()) {
            byte[] bytes = FileUtil.fileToBytes(audioFilePath);
            YSLog.d(TAG, "bytes = " + bytes.length);
            exeNetworkReq(KUploadAudioReqId, MeetingAPI.uploadAudio()
                    .courseId(courseId)
                    .detailId(mCourseDetailList.get(page).getString(TCourseDetail.id))
                    .pageNum(page)
                    .playType(type)
                    .file(bytes)
                    .build());
        }
    }

    public class WebSocketLink extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            YSLog.d(TAG, "onOpen:" + response.message());
            mWsSuccess = true; // 连接成功
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            YSLog.d(TAG, "onMessage:String" + text);
            runOnUIThread(() -> {
                try {
                    JSONObject ob = new JSONObject(text);
                    int order = ob.getInt("order");
                    if (order == 1) {
                        setCurrentItem(ob.getInt("pageNum"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            YSLog.d(TAG, "onMessage:ByteString" + bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            YSLog.d(TAG, "onClosing:" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            YSLog.d(TAG, "onClosed:" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            YSLog.d(TAG, "onFailure:");
            // 2秒后重连
            if (isFinishing()) {
                return;
            }
            // 没退出继续发任务
            runOnUIThread(() -> {
                if (isFinishing() || Util.noNetwork()) {
                    return;
                }
                // 没退出继续重连
                mWebSocket = exeWebSocketReq(NetworkReq.newBuilder(mWebSocketUrl).build(), new WebSocketLink());
            }, TimeUnit.SECONDS.toMillis(2));
        }
    }

}