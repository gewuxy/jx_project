package yy.doctor.ui.frag.meeting.course;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLMediaPlayer.OnBufferingUpdateListener;
import com.pili.pldroid.player.PLMediaPlayer.OnCompletionListener;
import com.pili.pldroid.player.PLMediaPlayer.OnErrorListener;
import com.pili.pldroid.player.PLMediaPlayer.OnPreparedListener;

import java.util.concurrent.TimeUnit;

import lib.player.NetVideoView;
import lib.player.NetVideoView.VideoViewListener;
import inject.annotation.router.Route;
import lib.ys.YSLog;
import lib.ys.util.view.LayoutUtil;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.R;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.util.Util;

/**
 * PPT视频
 *
 * @auther : GuoXuan
 * @since : 2017/6/28
 */
@Route
public class VideoCourseFrag extends BaseCourseFrag implements
        OnBufferingUpdateListener,
        OnPreparedListener,
        OnErrorListener,
        VideoViewListener,
        OnCompletionListener,
        OnCountDownListener {

    private RelativeLayout mLayout;
    private LinearLayout mLayoutLoad;
    private RelativeLayout mLayoutVideo;
    private NetVideoView mVideo;
    private String mVideoUrl;
    private long mRemainCount;
    private CountDown mCountDown;
    private boolean mIsFinish; // 结束了没有

    public void setRemainTime(int remainTime) {
        mRemainCount = remainTime;
        start();
    }

    @Override
    public void initData() {
        super.initData();

        mIsFinish = false;

        mCountDown = new CountDown();
        mCountDown.setListener(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_meeting_course_video;
    }

    @Override
    public void findViews() {
        mLayout = findView(R.id.meeting_course_video_layout);
        mLayoutLoad = findView(R.id.meeting_course_video_load);
        mLayoutVideo = findView(R.id.meeting_course_video_layout_video);
    }

    @Override
    public void setViews() {
        mVideoUrl = Util.convertUrl(getCourse().getString(TCourse.videoUrl));
        // 初始化NetVideoView
        initVideo();
        setOnClickListener(mLayout);
        setBackgroundColor(Color.TRANSPARENT);
    }

    private boolean initVideo() {
        if (mVideo == null) {

            addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    runOnUIThread(() -> {
                        // 初始化NetVideoView
                        mVideo = new NetVideoView(getContext());
                        mVideo.setOnBufferingUpdateListener(VideoCourseFrag.this);
                        mVideo.setOnPreparedListener(VideoCourseFrag.this);
                        mVideo.setOnProListener(VideoCourseFrag.this);
                        mVideo.setOnErrorListener(VideoCourseFrag.this);
                        mVideo.setOnCompletionListener(VideoCourseFrag.this);
                        mVideo.setBufferingIndicator(mLayoutLoad);

                        AVOptions options = new AVOptions();
                        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
                        mVideo.setAVOptions(options);
                        mVideo.setVideoPath(mVideoUrl);
                        showView(mLayoutLoad);

                        mLayoutVideo.addView(mVideo, 0, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT));
                    }, 300);

                    removeOnPreDrawListener(this);
                    return true;
                }
            });

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void toggle() {
        if (mVideo == null) {
            YSLog.d(TAG, "toggle 没有mp");
            return;
        }

        if (mVideo.isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void onClick(View v) {
        onCourseClick();
    }


    @Override
    public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int i) {

    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        mRemainCount = mVideo.getDuration() / TimeUnit.SECONDS.toMillis(1);
        onPrepared(mVideo.getDuration());

        start();
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        end();
        mIsFinish = true;
    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
        return mIsFinish;
    }

    @Override
    public void onVideoProgress(long progress) {

    }

    @Override
    public void onCountDown(long remainCount) {
        if (mVideo != null) {
            onProgress(mVideo.getCurrentPosition());
        }
    }

    @Override
    public void onCountDownErr() {
    }

    public void start() {
        if (!initComplete() || !getVisible() || mVideo == null) {
            return;
        }

        play();
        onPlay(true, mVideo.getDuration());
    }

    public void play() {
        if (getVisible()) {
            // 系统的isVisible()不准确
            if (mVideo == null) {
                return;
            }
            mVideo.start();
            mCountDown.start(mRemainCount);

            onPlay(true, mVideo.getDuration());
        }
    }

    @Override
    public boolean preparePlay() {
        if (!initComplete()) {
            return false;
        }

        if (mVideo != null && mVideo.isPlaying()) {
            mVideo.stopPlayback();
        }

        mVideo.setVideoPath(mVideoUrl);

        return true;
    }

    public void pause() {
        if (!initComplete()) {
            return;
        }

        mCountDown.stop();
        if (mVideo != null) {
            mVideo.pause();
            mRemainCount = (mVideo.getDuration() - mVideo.getCurrentPosition()) / 1000;
            onPlay(false, mVideo.getDuration());
        }
    }

    public void stop() {
        if (!initComplete()) {
            return;
        }

        mCountDown.stop();
        if (mVideo != null) {
            onPlayStop();
            mRemainCount = 0;
        }
    }

    public void seekTo(int msec) {
        if (mVideo != null) {
            mVideo.seekTo(msec);
        }
    }

    @Override
    protected void onPlayStop() {
        super.onPlayStop();

        if (mVideo != null) {
            mVideo.stopPlayback();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();

        preparePlay();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();

        stop();
    }

    @Override
    public void onPause() {
        super.onPause();

        pause();
    }

    @Override
    protected void finish() {
        super.finish();

        if (mVideo != null) {
            goneView(mVideo);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mVideo = null;
        stop();
    }
}
