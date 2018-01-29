package jx.csp.ui.activity.record;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.CallSuper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import inject.annotation.router.Arg;
import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.AudioUploadContract;
import jx.csp.dialog.CommonDialog1;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.meeting.Course.CourseType;
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
import jx.csp.util.CacheUtil;
import jx.csp.util.ScaleTransformer;
import jx.csp.util.Util;
import jx.csp.view.GestureView;
import jx.csp.view.StarBar;
import lib.jx.notify.LiveNotifier;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.notify.LiveNotifier.OnLiveNotify;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseVpActivity;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.receiver.ConnectionReceiver;
import lib.ys.receiver.ConnectionReceiver.OnConnectListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;

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

    private final int KMicroPermissionCode = 10;
    protected final int KOne = 1;
    private final int KVpSize = 3; // Vp缓存的数量
    private final int KDuration = 50; // 动画时长
    private final float KVpScale = 0.038f; // vp的缩放比例
    protected final int KBreathIntervalTime = 1500; // 设置呼吸灯时间间隔
    protected final int KNotifyId = 1;

    protected TextView mTvCurrentPage;
    protected TextView mTvTotalPage;
    protected TextView mTvRemind;
    protected TextView mTvRecordTime;
    protected TextView mTvRecordState;
    protected TextView mTvPlayTime;
    protected TextView mTvOnlineNum;

    protected ImageView mIvAudition;
    protected ImageView mIvRecordState;
    protected ImageView mIvRecordStateAlpha;
    protected ImageView mIvRerecording;

    protected GestureView mGestureView;
    protected SeekBar mSeekBar;
    protected StarBar mStarBar;
    protected View mLayoutTvRecordState;
    protected View mLayoutPlay;
    protected View mLayoutOnline;

    protected List<CourseDetail> mCourseDetailList;
    protected AudioUploadPresenterImpl mAudioUploadPresenter;
    protected PhoneStateListener mPhoneStateListener = null;  // 电话状态监听
    private ConnectionReceiver mConnectionReceiver;
    private ScaleTransformer mTransformer;

    protected boolean mRecordPermissionState = false;  // 是否有录音权限

    protected NotificationCompat.Builder mBuilder;
    protected NotificationManager mManager;

    protected AlphaAnimation mAnimationFadeIn;
    protected AlphaAnimation mAnimationFadeOut;
    protected Meet mShareAndStarArg;

    protected boolean mStarState = false;  // 是否有星评
    protected boolean mSlideFinish = false;  // 是否滑动结束

    protected boolean mIsSet = false;  // 是否去设置

    @Arg
    String mCourseId;  // 课程id

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

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(getString(R.string.record_ing));
        mBuilder.setAutoCancel(false);
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.nav_bar_ic_back, v -> onBackPressed());
        View view = inflate(R.layout.layout_base_record_nav_bar);
        mTvCurrentPage = view.findViewById(R.id.layout_base_record_nav_bar_tv_current_page);
        mTvTotalPage = view.findViewById(R.id.layout_base_record_nav_bar_tv_total_page);
        bar.addViewMid(view);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            if (mShareAndStarArg != null) {
                ShareDialog dialog = new ShareDialog(this, mShareAndStarArg);
                dialog.show();
            }
        });
        Util.addDivider(bar);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_base_record;
    }

    @Override
    public void findViews() {
        super.findViews();

        mGestureView = findView(R.id.gesture_view);

        mTvRemind = findView(R.id.record_tv_remind);
        mTvRecordTime = findView(R.id.record_tv_time);

        mIvAudition = findView(R.id.record_iv_audition);
        mIvRecordStateAlpha = findView(R.id.record_iv_state_alpha);
        mIvRecordState = findView(R.id.record_iv_state);
        mTvRecordState = findView(R.id.record_tv_state);
        mIvRerecording = findView(R.id.record_iv_rerecording);

        mSeekBar = findView(R.id.record_seek_bar);
        mTvPlayTime = findView(R.id.record_tv_play_time);
        mStarBar = findView(R.id.record_star_bar);

        mLayoutPlay = findView(R.id.record_play_layout);
        mLayoutTvRecordState = findView(R.id.record_state_tv_layout);
        mLayoutOnline = findView(R.id.record_online_layout);
        mTvOnlineNum = findView(R.id.record_tv_online_num);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        // 检查录音权限
        if (checkPermission(KMicroPermissionCode, Permission.micro_phone)) {
            havePermissionState();
            mRecordPermissionState = true;
        } else {
            noPermissionState();
            mRecordPermissionState = false;
        }

        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //NetworkImageView.clearMemoryCache(BaseRecordActivity.this);
                mTvCurrentPage.setText(String.valueOf(getCurrPosition() + KOne));
                if (position == (mCourseDetailList.size() - 1)) {
                    showView(mStarBar);
                } else {
                    goneView(mStarBar);
                }
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

        mAnimationFadeIn = new AlphaAnimation(0.2f, 1.0f);
        mAnimationFadeIn.setDuration(KBreathIntervalTime);
        mAnimationFadeIn.setFillAfter(false);  //动画结束后不保持状态
        mAnimationFadeOut = new AlphaAnimation(1.0f, 0.2f);
        mAnimationFadeOut.setDuration(KBreathIntervalTime);
        mAnimationFadeOut.setFillAfter(false);
        mAnimationFadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                startAnimationFadeOut();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });
        mAnimationFadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                startAnimationFadeIn();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        mConnectionReceiver.register();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsSet) {
            mIsSet = false;
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 检查录音权限
                    if (checkPermission(KMicroPermissionCode, Permission.micro_phone)) {
                        havePermissionState();
                        mRecordPermissionState = true;
                        refreshView();
                    } else {
                        noPermissionState();
                        mRecordPermissionState = false;
                    }
                    removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (getCount() == 0) {
            return;
        }
        onClick(v.getId());
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
        if (!mSlideFinish) {
            CommonServRouter.create(ReqType.exit_record)
                    .courseId(mCourseId)
                    .pageNum(getCurrPosition())
                    .overType(0)
                    .route(this);
        }
        // 注销服务
        if (mConnectionReceiver != null) {
            mConnectionReceiver.unRegister();
        }
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

    @Override
    public void onPermissionResult(int code, int result) {
        if (code == KMicroPermissionCode) {
            switch (result) {
                case PermissionResult.granted:
                    havePermissionState();
                    mRecordPermissionState = true;
                    break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    noPermissionState();
                    noRecordPermissionDialog();
                    mRecordPermissionState = false;
                }
                break;
            }
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
                        onCallRinging();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        YSLog.d(TAG, "call state off hook " + state);
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    protected void noRecordPermissionDialog() {
        CommonDialog1 dialog = new CommonDialog1(this);
        dialog.setTitle(R.string.dialog_title_hint);
        dialog.setContent(R.string.no_record_permission);
        dialog.addBlackButton(R.string.return_home, v -> finish());
        dialog.addBlackButton(R.string.to_open_permission, v -> {
            mIsSet = true;
            try {
                // 应用详情页面
                Intent i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction(ConstantsEx.KSystemSetting);
                Uri uri = Uri.fromParts(ConstantsEx.KPackage, App.getContext().getPackageName(), null);
                i.setData(uri);
                App.getContext().startActivity(i);
            } catch (Exception e) {
                // 设置页面
                Log.e(TAG, Log.getStackTraceString(e));
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(intent);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 上传音频文件
     *
     * @param courseId
     * @param page
     * @param type
     */
    protected void uploadAudioFile(String courseId, int page, @CourseType int type) {
        String audioFilePath = CacheUtil.getExistAudioFilePath(courseId, mCourseDetailList.get(page).getInt(TCourseDetail.id));
        uploadAudioFile(courseId, page, type, audioFilePath, 0);
    }

    protected void uploadAudioFile(String courseId, int page, @CourseType int type, String audioFilePath, int time) {
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

    abstract protected void havePermissionState();

    abstract protected void noPermissionState();

    abstract protected void refreshView();

    abstract protected void onClick(int id);

    abstract protected void pageSelected(int position);

    abstract protected void onCallRinging();

    abstract protected void switchDevice();

    abstract protected void startAnimationFadeIn();

    abstract protected void startAnimationFadeOut();
}
