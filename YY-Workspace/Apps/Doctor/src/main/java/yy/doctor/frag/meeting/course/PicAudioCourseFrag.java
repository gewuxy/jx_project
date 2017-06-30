package yy.doctor.frag.meeting.course;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

import lib.ys.YSLog;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.util.TextUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.R;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;
import yy.doctor.view.RootLayout;
import yy.doctor.view.RootLayout.OnRootTouchListener;

/**
 * 同时能播放音频和查看图片
 *
 * @auther yuansui
 * @since 2017/6/7
 */
public class PicAudioCourseFrag extends BaseCourseFrag implements
        OnCompletionListener,
        OnCountDownListener,
        OnRootTouchListener {

    private NetworkPhotoView mIvPPT;
    private ImageView mIvHolder;

    // 是否存在已下载好的音频
    private boolean mAudioExist = false;
    private int mRemainTime; // 剩余时间
    private String mFile; // 文件的路径

    private MediaPlayer mMp;
    private CountDown mCountDown;
    protected RootLayout mLayout;
    private boolean mIsFinish;
    private boolean mExist; // 页面存在

    public void setRemainTime(int remainTime) {
        mRemainTime = remainTime;
        start();
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_meeting_course_pic;
    }

    @Override
    public void initData() {
        super.initData();

        mExist = true;
    }

    @Override
    public void findViews() {
        mIvPPT = findView(R.id.meeting_course_pic_iv);
        mIvHolder = findView(R.id.meeting_course_pic_iv_place_holder);
        mLayout = findView(R.id.meeting_course_pic_layout);
    }

    @Override
    public void setViews() {
        setPic();
        setAudio();
        mLayout.setOnRootTouchListener(this);

        setBackgroundColor(Color.TRANSPARENT);
    }

    public ImageView getIvHolder() {
        return mIvHolder;
    }

    protected void setPic() {
        String imgUrl = getDetail().getString(TCourse.imgUrl);

        if (!TextUtil.isEmpty(imgUrl)) {
            mIvPPT.url(imgUrl)
                    .listener(new NetworkImageListener() {
                        @Override
                        public void onImageSet(ImageInfo info) {
                            // 加载成功隐藏默认图
                            goneView(mIvHolder);
                        }
                    })
                    .load();
        }
    }

    protected void setAudio() {
        mIsFinish = false;
        String audioUrl = getDetail().getString(TCourse.audioUrl);

        // 文件名
        String type = audioUrl.substring(audioUrl.lastIndexOf(".") + 1);
        String fileName = audioUrl.hashCode() + "." + type;
        // 文件夹名字
        String filePath = CacheUtil.getMeetingCacheDir(getMeetId());
        mFile = filePath + fileName;

        File file = CacheUtil.getMeetingCacheFile(getMeetId(), fileName);

        if (!file.exists()) {
            // 不存在下载
            exeNetworkReq(NetFactory.newDownload(audioUrl, filePath, fileName).build());
        } else {
            // 存在可以播放
            mAudioExist = true;
            preparePlay();

            if (getVisible()) { // 系统的isVisible()不准确
                start();
            }
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        mAudioExist = true;
        if (getVisible() && mExist) {
            start();
        }
    }

    @Override
    public boolean isFinish() {
        return mIsFinish;
    }

    /**
     * 准备播放
     */
    public boolean preparePlay() {
        if (mAudioExist) {
            if (mMp == null) {
                mMp = new MediaPlayer();
                mMp.setOnCompletionListener(this);
                mMp.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mMp.reset();
                    mMp.setDataSource(mFile);
                    mMp.prepare();
                    mRemainTime = mMp.getDuration() / 1000;
                } catch (Exception e) {
                    YSLog.e(TAG, "preparePlay", e);
                }
            }
            onPrepared(mMp.getDuration());
            return true;
        }
        return false;
    }

    @Override
    public void toggle() {
        if (mMp == null) {
            YSLog.d(TAG, "toggle 没有mp");
            return;
        }

        if (mMp.isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();

        start();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();

        NetworkImageView.clearMemoryCache(getContext());

        stop();
    }

    @Override
    protected void onPlayStop() {
        super.onPlayStop();

        if (mMp != null) {
            mMp.reset();
            mMp = null;
        }
    }

    /**
     * 开始播放音频
     */
    public void play() {
        if (getVisible()) { // 系统的isVisible()不准确
            mMp.start();
            countStop();
            if (mCountDown == null) {
                mCountDown = new CountDown(mRemainTime);
                mCountDown.setListener(this);
            }
            mCountDown.start();
            onPlay(true, mMp.getDuration());
        }
    }

    public void start() {
        if (mMp != null && mMp.isPlaying()) {
            mMp.stop();
        }
        if (preparePlay()) {
            play();
        }
    }

    public void pause() {
        if (mMp != null && mMp.isPlaying()) {
            mMp.pause();
            mRemainTime = (mMp.getDuration() - mMp.getCurrentPosition()) / 1000;
        }
        countStop();
        onPlay(false, mMp.getDuration());
    }

    public void stop() {
        if (mMp != null) {
            onPlayStop();
            mRemainTime = 0;
        }
        countStop();
    }

    public void seekTo(int msec) {
        if (mMp != null) {
            mMp.seekTo(msec);
        }
    }

    public void countStop() {
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onPlayStop();
        mIsFinish = true;
        mAudioExist = true;
        preparePlay();
        end();
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void onCountDown(long remainCount) {
        if (mMp != null) {
            onProgress(mMp.getCurrentPosition());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        preparePlay();
    }

    @Override
    public void onPause() {
        super.onPause();

        notify(NotifyType.preparePlay);
        mExist = false;
        onPlayStop();
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mExist = false;
        if (mMp != null) {
            mMp.stop();
            mMp = null;
        }
    }

    @Override
    public void onTouchUp() {
        onCourseClick();
    }

}
