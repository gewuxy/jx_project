package yy.doctor.frag.meeting.course;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

import lib.ys.LogMgr;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.util.TextUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.R;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;

/**
 * 同时能播放音频和查看图片
 *
 * @auther yuansui
 * @since 2017/6/7
 */
public class PicAudioCourseFrag extends BaseCourseFrag implements OnCompletionListener, OnCountDownListener {

    // TODO: 2017/6/6 切换音乐

    private NetworkPhotoView mIvPPT;
    private ImageView mIvDefault;

    // 是否存在已下载好的音频
    private boolean mAudioExist = false; // onVisible在setViews之前调用

    private int mRemainTime; // 剩余时间
    private String mFile; // 文件的路径

    private MediaPlayer mMp;
//    private CountDown mCountDown;


    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_meeting_course_pic;
    }

    @Override
    public void findViews() {
        mIvPPT = findView(R.id.meeting_ppt_pic_iv);
        mIvDefault = findView(R.id.meeting_ppt_pic_iv_place_holder);
    }

    protected void setPic() {
        String imgUrl = getDetail().getString(TCourse.imgUrl);

        if (!TextUtil.isEmpty(imgUrl)) {
            mIvPPT.url(imgUrl)
                    .listener(new NetworkImageListener() {
                        @Override
                        public void onImageSet(ImageInfo info) {
                            // 加载成功隐藏默认图
                            goneView(mIvDefault);
                        }
                    })
                    .load();
        }
    }

    protected void setAudio() {
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
        }
    }

    @Override
    public void setViews() {
        setPic();
        setAudio();
        onPrepare(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onPlayStop();
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        LogMgr.d(TAG, mFile + "下载完成");
        mAudioExist = true;
        start();
    }

    @Override
    public void toggle() {
        if (mMp == null) {
            LogMgr.d(TAG, "toggle 没有mp");
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
        LogMgr.d(TAG, mFile + "------------Visible");
        start();
    }

    @Override
    protected void onInvisible() {
        LogMgr.d(TAG, mFile + "------------Invisible");
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
     * 准备播放
     */
    private boolean preparePlay() {
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
                    LogMgr.e(TAG, "preparePlay", e);
                }
            } else {
                return true;
            }

//            Observable.fromCallable(() -> {
//                mMp.reset();
//                mMp.setDataSource(mFile);
//                mMp.prepare();
//                mRemainTime = mMp.getDuration() / 1000;
//
//                LogMgr.d(TAG, "play() file = " + mFile);
//                LogMgr.d(TAG, "play() mMp.getDuration() = " + mMp.getDuration());
//                LogMgr.d(TAG, "play() r time = " + mRemainTime);
//
//                return mMp;
//            }).subscribeOn(AndroidSchedulers.mainThread())
////                    .doOnSubscribe(disposable -> onPrepare(true))
////                    .subscribeOn(AndroidSchedulers.mainThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(mediaPlayer -> onPrepare(true));
//            Observable.just(mMp)
//                    .doOnSubscribe(subscription -> {
//                        mMp.reset();
//                        mMp.setDataSource(mFile);
//                        mMp.prepare();
//                        mRemainTime = mMp.getDuration() / 1000;
//                        onPrepare(true);
//
//
//                    })
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(mediaPlayer -> {
//                        onStart(true);
//                    });
            return true;
        }

        return false;
    }

    /**
     * 开始播放音频
     */
    private void play() {
        if (getVisible()) {
            mMp.start();
        }
    }

    private void start() {
        if (preparePlay()) {
            play();
//        countStop();
//        if (mCountDown == null) {
//            mCountDown = new CountDown(mRemainTime);
//            mCountDown.setListener(this);
//        }
//        mCountDown.start();
        }
    }

    private void stop() {
        if (mMp != null) {
            onPlayStop();
            mRemainTime = 0;
//            mRemainTime = (mMp.getDuration() - mMp.getCurrentPosition()) / 1000;
        }
//        countStop();
    }

    private void pause() {
        if (mMp != null && mMp.isPlaying()) {
            mMp.pause();
            mRemainTime = (mMp.getDuration() - mMp.getCurrentPosition()) / 1000;
        }
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

//    public void countStop() {
//        if (mCountDown != null) {
//            mCountDown.stop();
//        }
//    }

//    private MediaPlayer getMp() {
//        if (mMp == null) {
//            mMp = new MediaPlayer();
//            mMp.setOnCompletionListener(this);
//            mMp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        }
//        return mMp;
//    }


    @Override
    public void onStop() {
        super.onStop();
        onPlayStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMp != null) {
            mMp.release();
        }
    }
}
