package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.LayoutUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.Constants.DateUnit;
import yy.doctor.Extra;
import yy.doctor.R;
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
    private boolean mIsPortrait; // 屏幕方向
    private String mUriString; // 播放地址
    private CountDown mCountDown; // 倒计时

    private View mView; // 占NavBar的view
    private View mFunction; // 功能栏
    private View mLayout; // 整个界面
    private View mLoading; // 加载中的视图
    private SeekBar mSbProgress; // 进度条
    private TextView mTvTime; // 显示播放时间
    private ImageView mIvControl; // 控制播放暂停
    private NetVideoView mVideo; // 播放控件
    private RelativeLayout mLayoutVideo; // 播放容器

    public static void nav(Context context, String url) {
        Intent i = new Intent(context, VideoActivity.class)
                .putExtra(Extra.KData, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mUriString = getIntent().getStringExtra(Extra.KData);
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
                goneView(mFunction);
                finish();
            } else {
                toPortrait();
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

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void findViews() {
        mView = findView(R.id.meeting_ppt_view);
        mLayout = findView(R.id.video_layout);
        mLayoutVideo = findView(R.id.video_layout_video);
        mFunction = findView(R.id.video_layout_function);
        mIvControl = findView(R.id.video_iv_control);
        mTvTime = findView(R.id.video_tv_time);
        mSbProgress = findView(R.id.meeting_ppt_sb_progress);
        mLoading = findView(R.id.video_loading);
    }

    @Override
    public void setViews() {
        setOnClickListener(mLayout);
        setOnClickListener(mIvControl);

        touch(mFunction);
        touch(mIvControl);
        touch(mTvTime);

        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvTime.setText(Util.formatTime((long) (mSbProgress.getProgress() / 100.0 * mAllTime), DateUnit.minute));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mCountDown != null) {
                    mCountDown.stop();
                }
                mVideo.pause();
                mVideo.recycle();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long playTime = (long) (mSbProgress.getProgress() / 100.0 * mAllTime);
                mVideo.prepared(mAllTime - playTime);
                mVideo.seekTo(playTime * 1000);
                mVideo.start();
                mIvControl.setSelected(false);
                countDown();
            }
        });

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

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
                    mVideo.setBufferingIndicator(mLoading);
                    mVideo.setVideoPath(mUriString);
                    showView(mLoading);
                    showView(mFunction);
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
     * 控件按下取消倒数消失
     *
     * @param view
     */
    private void touch(View view) {
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
            return false;
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
    public void onCountDownErr() {

    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            if (!mIsPortrait) {
                goneView(getNavBar());
            }
            goneView(mFunction);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_iv_control: {
                if (mVideo.getCurrentPosition() == mVideo.getDuration()) {
                    mVideo.prepared(mAllTime);
                }
                mVideo.toggleState();
                mIvControl.setSelected(!mIvControl.isSelected());
            }
            break;
            case R.id.video_layout: {
                if (mFunction.getVisibility() == View.VISIBLE) {
                    goneView(mFunction);
                    if (!mIsPortrait) { // 横屏
                        goneView(getNavBar());
                    }
                    mCountDown.stop();
                } else {
                    showView(mFunction);
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
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
            mIsPortrait = true;
            mVideo.rotatePortrait(fitDp(KVideoHDp));

            showView(mView);
            showView(getNavBar());
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            mIsPortrait = false;
            mVideo.rotateLandscape();

            goneView(mView);
            if (mFunction.getVisibility() == View.GONE) {
                goneView(getNavBar());
            }
        }
    }

    @Override
    public void onVideoProgress(long progress) {
        int percent = (int) (progress * 100.0 / mAllTime);
        mSbProgress.setProgress(percent);
        mTvTime.setText(Util.formatTime(progress, DateUnit.minute));
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        // 开始倒计时,总时长
        mAllTime = plMediaPlayer.getDuration() / 1000;
        mVideo.prepared(mAllTime);
    }

    @Override
    public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int i) {
        mSbProgress.setSecondaryProgress(i);
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
                showToast("播放错误");
                return false;
        }
        return true;
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0); // 第一次播放后不自动播放
        mVideo.setAVOptions(options);
        mVideo.setVideoPath(mUriString);
        mVideo.recycle();
        mIvControl.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        goneView(mLayoutVideo);
        goneView(mFunction);
        if (!mIsPortrait) {
            toPortrait();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideo != null) {
            mVideo.recycle();
            mVideo.stopPlayback();
        }
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }
}
