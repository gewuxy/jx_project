package yy.doctor.frag.meeting;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lib.ys.LogMgr;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.util.TextUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.R;
import yy.doctor.model.meet.Detail.TDetail;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;

/**
 * PPT图片+音频
 *
 * @auther : GuoXuan
 * @since : 2017/6/3
 */

public class MeetingPPTPicFrag extends BaseMeetingPPTFrag implements OnCompletionListener, OnCountDownListener {
    // TODO: 2017/6/6 切换音乐

    private NetworkPhotoView mIvPpt;
    private ImageView mIvDefault;

    private boolean mCanPlay = false; // onVisible在setViews之前调用
    private boolean mVisible = false; // 没下载之前显示
    private int mRemainTime; // 剩余时间
    private String mFile; // 文件的路径
    private OnPPTPicListener mOnPPTPicListener;
    private MediaPlayer mMp;
//    private CountDown mCountDown;


    public interface OnPPTPicListener {
        void OnAudio(boolean has, int all);

        void OnPlay(long time);
    }

    public void setOnPPTPicListener(OnPPTPicListener onPPTPicListener) {
        mOnPPTPicListener = onPPTPicListener;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_meeting_ppt_pic;
    }

    @Override
    public void findViews() {
        mIvPpt = findView(R.id.meeting_ppt_pic_iv);
        mIvDefault = findView(R.id.meeting_ppt_iv_default);
    }

    @Override
    public void setViews() {
        String imgUrl = mDetail.getString(TDetail.imgUrl);

        if (!TextUtil.isEmpty(imgUrl)) {
            mIvPpt.url(imgUrl)
                    .listener(new NetworkImageListener() {
                        @Override
                        public void onImageSet(ImageInfo info) {
                            // 加载成功隐藏默认图
                            goneView(mIvDefault);
                        }
                    })
                    .load();
        }

        String audioUrl = mDetail.getString(TDetail.audioUrl);
        if (!TextUtil.isEmpty(audioUrl)) {
            // 有音频资源
            mMp = new MediaPlayer();
            mMp.setOnCompletionListener(this);
            mMp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // 文件名
            String type = audioUrl.substring(audioUrl.lastIndexOf(".") + 1);
            String fileName = audioUrl.hashCode() + "." + type;
            // 文件夹名字
            String filePath = CacheUtil.getMeetingCacheDir(mMeetId);
            mFile = filePath + fileName;

            File file = CacheUtil.getMeetingCacheFile(mMeetId, fileName);

            if (!file.exists()) {
                // 不存在下载
                exeNetworkReq(NetFactory.newDownload(audioUrl, filePath, fileName).build());
            } else {
                // 存在可以播放
                mCanPlay = true;
                play();
            }
        } else {
            // 没有音频资源
            if (mOnPPTPicListener != null) {
                mOnPPTPicListener.OnAudio(false, 0);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMp.release();
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        if (progress == 100) {
            LogMgr.d(TAG, mFile + "下载完成");
            mCanPlay = true;
            if (mVisible) {
                play();
            }
        }
    }

    @Override
    public void toggle() {
        /*if (mMp != null) {
            if (mMp.isPlaying()) {
                stop();
            } else {
                start();
            }
        }*/
    }

    @Override
    protected void onVisible() {
        LogMgr.d(TAG,mFile + "------------Visible");
        mVisible = true;
        play();
    }

    @Override
    protected void onInvisible() {
        LogMgr.d(TAG,mFile + "------------Invisible");
        mVisible = false;
        if (mMp != null) {
            stop();
            mMp.release();
        }
    }

    /**
     * 初始播放
     */
    private void play() {
        if (mMp != null && mCanPlay) {
            Observable.just(mMp)
                    .doOnSubscribe(subscription -> {
                        mMp.reset();
                        mMp.setDataSource(mFile);
                        mMp.prepare();
                        mRemainTime = mMp.getDuration() / 1000;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mediaPlayer -> {
                        start();
                        if (mOnPPTPicListener != null) {
                            mOnPPTPicListener.OnAudio(true, mMp.getDuration());
                        }
                    });
        }
    }

    private void start() {
        mMp.start();
//        countStop();
//        if (mCountDown == null) {
//            mCountDown = new CountDown(mRemainTime);
//            mCountDown.setListener(this);
//        }
//        mCountDown.start();
    }

    private void stop() {
        mMp.stop();
//        countStop();
        mRemainTime = (mMp.getDuration() - mMp.getCurrentPosition()) / 1000;
    }

    @Override
    public void onCountDownErr() {

    }

    @Override
    public void onCountDown(long remainCount) {
        if (mOnPPTPicListener != null) {
            mOnPPTPicListener.OnPlay(mMp.getCurrentPosition());
        }
    }

//    public void countStop() {
//        if (mCountDown != null) {
//            mCountDown.stop();
//        }
//    }
}
