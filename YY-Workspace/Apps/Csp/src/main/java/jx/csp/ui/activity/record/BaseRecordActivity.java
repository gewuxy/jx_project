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
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import jx.csp.R;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.meeting.Course;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.WebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.TWebSocketMsg;
import jx.csp.model.meeting.WebSocketMsg.WsOrderFrom;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
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
    protected final int KUploadAudioReqId = 20;
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

    private float mLastOffset;
    protected PhoneStateListener mPhoneStateListener = null;  // 电话状态监听

    protected JoinMeeting mJoinMeeting; // 全部数据
    protected Course mCourseMsg;
    protected ArrayList<CourseDetail> mCourseDetailList;

    protected WebSocket mWebSocket;
    protected boolean mWsSuccess = false; // WebSocket连接是否成功
    protected String mWebSocketUrl; // WebSocket地址

    private LinkedList<NetworkReq> mUploadList;  // 上传音频队列
    private boolean mUploadState = false; // 是否在上传音频
    protected int mWsPosition = 0;  // websocket接收到的页数

    @Arg
    String mCourseId;  // 课程id
    String mTitle;

    @IntDef({
            FragType.img,
            FragType.video,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface FragType {
        int img = 1;  // 图片
        int video = 2;  // 视频
    }

    @CallSuper
    @Override
    public void initData() {
        //创建文件夹存放音频
        FileUtil.ensureFileExist(CacheUtil.getAudioCacheDir() + File.separator + mCourseId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            ShareDialog dialog = new ShareDialog(this, mTitle, Integer.valueOf(mCourseId));
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
        setOnPageChangeListener(this);
        setOnClickListener(R.id.record_iv_last);
        setOnClickListener(R.id.record_iv_next);
        mUploadList = new LinkedList<>();
        //setPageTransformer(false, new ScaleTransformer(KVpScale));
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

    protected void setNavBarMidText(String str) {
        if (mTvNavBar == null) {
            mTvNavBar = getNavBar().addTextViewMid(str);
        } else {
            mTvNavBar.setText(str);
        }
    }

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
     *
     * @param courseId
     * @param page
     * @param type
     */
    protected void uploadAudioFile(String courseId, int page, int type) {
        String audioFilePath = CacheUtil.getAudioPath(courseId, page);
        File file = new File(audioFilePath);
        if (file.exists()) {
            byte[] bytes = FileUtil.fileToBytes(audioFilePath);
            YSLog.d(TAG, "bytes = " + bytes.length);
            NetworkReq req = MeetingAPI.uploadAudio()
                    .courseId(courseId)
                    .detailId(mCourseDetailList.get(page).getString(TCourseDetail.id))
                    .pageNum(page)
                    .playType(type)
                    .file(bytes)
                    .build();
            mUploadList.addLast(req);
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

    @CallSuper
    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KUploadAudioReqId) {
            YSLog.d(TAG, "移除任务");
            mUploadList.removeFirst();
            mUploadState = false;
            upload();
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
                    String from = ob.getString("orderFrom");
                    if (order == WsOrderType.sync && from.equals(WsOrderFrom.web)) {
                        mWsPosition = ob.getInt("pageNum");
                        setCurrentItem(mWsPosition);
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

    /**
     * 同步指令 如果跟上次websocket发过来的页数一致就不需要发同步指令了
     *
     * @param position
     */
    protected void webSocketSendMsg(int position) {
        if (position != mWsPosition) {
            WebSocketMsg msg = new WebSocketMsg();
            msg.put(TWebSocketMsg.courseId, mCourseId);
            msg.put(TWebSocketMsg.order, WsOrderType.sync);
            msg.put(TWebSocketMsg.orderFrom, WsOrderFrom.app);
            msg.put(TWebSocketMsg.pageNum, position);
            mWebSocket.send(msg.toJson());
        }
    }

}
