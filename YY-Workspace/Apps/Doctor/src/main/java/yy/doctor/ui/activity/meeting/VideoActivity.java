package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLMediaPlayer.OnBufferingUpdateListener;
import com.pili.pldroid.player.PLMediaPlayer.OnCompletionListener;
import com.pili.pldroid.player.PLMediaPlayer.OnErrorListener;
import com.pili.pldroid.player.PLMediaPlayer.OnPreparedListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import lib.player.NetVideoView;
import lib.player.NetVideoView.VideoViewListener;
import lib.ys.YSLog;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.LayoutUtil;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
import yy.doctor.model.meet.video.Detail;
import yy.doctor.model.meet.video.Detail.TDetail;
import yy.doctor.serv.CommonServ;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.util.Time;
import yy.doctor.util.Util;

/**
 * 纯视频播放界面
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */
public class VideoActivity extends BaseActivity implements
        OnBufferingUpdateListener,
        OnPreparedListener,
        OnErrorListener,
        VideoViewListener,
        OnCompletionListener,
        OnCountDownListener {

    private static final int KVideoHDp = 204; // 视频高度
    private static final int KVanishTime = 3; // 自动隐藏功能栏时间

    private long mAllTime; // 视频总时长(s)
    private long mDuration; // 本次学习时长
    private boolean mIsPortrait; // 屏幕方向
    private String mUriString; // 播放地址
    private Detail mDetail;
    private CountDown mCountDown; // 倒计时

    private View mViewAll; // 整个界面
    private View mViewLoad; // 加载中的视图
    private View mViewNavBar; // 占NavBar的view
    private View mViewFunction; // 功能栏
    private SeekBar mSbProgress; // 进度条
    private TextView mTvTime; // 显示播放时间
    private ImageView mIvControl; // 控制播放暂停
    private NetVideoView mVideo; // 播放控件
    private RelativeLayout mLayoutVideo; // 播放容器
    private Submit mSubmit; // 提交记录时间的需要数据

    private boolean mFirst; // 第一次自动播放

    public static void nav(Context context, Detail detail, Submit submit) {
        Intent i = new Intent(context, VideoActivity.class)
                .putExtra(Extra.KData, detail)
                .putExtra(Extra.KSubmit, submit);
        LaunchUtil.startActivityForResult(context, i, 0);
    }

    @Override
    public void initData() {
        mDetail = (Detail) getIntent().getSerializableExtra(Extra.KData);
        mSubmit = (Submit) getIntent().getSerializableExtra(Extra.KSubmit);
        mUriString = Util.convertUrl(mDetail.getString(TDetail.url).trim());
        mDuration = 0;
        mFirst = true;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_video;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundColor(Color.TRANSPARENT);
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, v -> {
            if (mIsPortrait) {
                goneView(mLayoutVideo);
                goneView(mViewFunction);
                finish();
            } else {
                toPortrait();
            }
        });
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void findViews() {
        mTvTime = findView(R.id.video_tv_time);
        mIvControl = findView(R.id.video_iv_control);
        mSbProgress = findView(R.id.video_sb);

        mViewAll = findView(R.id.video_layout);
        mViewLoad = findView(R.id.video_layout_load);
        mLayoutVideo = findView(R.id.video_layout_video);
        mViewNavBar = findView(R.id.video_view_nav_bar);
        mViewFunction = findView(R.id.video_layout_function);
    }

    @Override
    public void setViews() {
        setOnClickListener(mViewAll);
        setOnClickListener(mIvControl);

        touch(mViewFunction, false);
        touch(mIvControl, false);
        touch(mTvTime, false);
        touch(mSbProgress, true);

        // 添加VideoView
        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                runOnUIThread(() -> {
                    // 初始化NetVideoView
                    mVideo = new NetVideoView(VideoActivity.this);
                    mVideo.setOnBufferingUpdateListener(VideoActivity.this);
                    mVideo.setOnPreparedListener(VideoActivity.this);
                    mVideo.setOnProListener(VideoActivity.this);
                    mVideo.setOnErrorListener(VideoActivity.this);
                    mVideo.setOnCompletionListener(VideoActivity.this);
                    mVideo.setBufferingIndicator(mViewLoad);
                    AVOptions options = new AVOptions();
                    options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0); // 设置不自动播放
                    mVideo.setAVOptions(options);
                    mVideo.setVideoPath(mUriString);
                    showView(mViewLoad);
                    showView(mViewFunction);
                    countDown();

                    mIsPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                    int videoH = mIsPortrait ? fitDp(KVideoHDp) : LayoutUtil.MATCH_PARENT; // 区分横竖屏高度
                    mLayoutVideo.addView(mVideo, 0, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, videoH));
                }, 300);

                removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    /**
     * 切换为竖屏
     */
    private void toPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Observable.just((Runnable) () ->
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)) // 设置回默认值
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribe(Runnable::run);
    }

    /**
     * 控件按下取消倒数消失
     *
     * @param view
     */
    private void touch(View view, boolean dispose) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (mCountDown != null) {
                        mCountDown.stop();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    countDown();
                    break;
            }
            return dispose;
        });
    }

    /**
     * 倒计时
     */
    private void countDown() {
        if (mCountDown == null) {
            mCountDown = new CountDown(KVanishTime);
            mCountDown.setListener(VideoActivity.this);
        }
        mCountDown.start();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            if (!mIsPortrait) {
                goneView(getNavBar());
            }
            goneView(mViewFunction);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_iv_control: {
                mVideo.toggleState();
                mIvControl.setSelected(!mIvControl.isSelected());
            }
            break;

            case R.id.video_layout: {
                if (mViewFunction.getVisibility() == View.VISIBLE) {
                    goneView(mViewFunction);
                    if (!mIsPortrait) { // 横屏
                        goneView(getNavBar());
                    }
                    mCountDown.stop();
                } else {
                    showView(mViewFunction);
                    showView(getNavBar());
                    countDown();
                }
            }
            break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            mIsPortrait = true;
            mVideo.rotatePortrait(fitDp(KVideoHDp));

            showView(mViewNavBar);
            showView(getNavBar());
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            mIsPortrait = false;
            mVideo.rotateLandscape();

            goneView(mViewNavBar);
            if (mViewFunction.getVisibility() == View.GONE) {
                goneView(getNavBar());
            }
        }
    }

    @Override
    public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int i) {
        mSbProgress.setSecondaryProgress(i);
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        // 总时长
        mAllTime = plMediaPlayer.getDuration() / TimeUnit.SECONDS.toMillis(1);
        if (mFirst) {
            // 第一次看
            long start = mDetail.getLong(TDetail.userdtime, 0) % mAllTime; // 开始时间(s)
            long residue = mAllTime - start; // 剩余时间(s)
            mSbProgress.setProgress((int) (start * 100.0 / mAllTime));
            mTvTime.setText(Time.secondFormat(start, DateUnit.minute));
            mVideo.seekTo(start * TimeUnit.SECONDS.toMillis(1));
            mVideo.start();
            // 开始倒计时
            mVideo.prepared(residue);
        }
    }

    @Override
    public void onVideoProgress(long progress) {
        mDuration++;
        YSLog.d(TAG, "onCountDown:mDuration=" + mDuration);
        int percent = (int) (progress * 100.0 / mAllTime);
        mSbProgress.setProgress(percent);
        mTvTime.setText(Time.secondFormat(progress, DateUnit.minute));
    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
        switch (errorCode) {
            case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                showToast("未知错误");
                break;
            case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                showToast("网络异常");
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                showToast("连接超时");
                break;
            case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                showToast("播放器准备超时");
                break;
            default:
                showToast("播放资源错误");
                break;
        }
        return true;
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        mVideo.setVideoPath(mUriString);
        mVideo.recycle();
        mVideo.prepared(mAllTime);
        mIvControl.setSelected(true);
        goneView(mViewLoad);
        mFirst = false;
    }

    @Override
    public void onBackPressed() {
        goneView(mLayoutVideo);
        goneView(mViewFunction);
        if (!mIsPortrait) {
            toPortrait();
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra(Extra.KData, mDuration));
        boolean finish = mDetail.getLong(TDetail.userdtime, 0) + mDuration > mAllTime;
        mSubmit.put(TSubmit.usedtime, mDuration + mDetail.getLong(TDetail.userdtime, 0));
        mSubmit.put(TSubmit.finished, finish);
        Intent intent = new Intent(this, CommonServ.class)
                .putExtra(Extra.KType, ReqType.video)
                .putExtra(Extra.KData, mSubmit);
        startService(intent);
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mVideo != null) {
            mVideo.toggleState();
            mIvControl.setSelected(!mIvControl.isSelected());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mVideo != null) {
            mVideo.recycle();
            mVideo.stopPlayback();
        }
        if (mCountDown != null) {
            mCountDown.recycle();
        }
    }
}
