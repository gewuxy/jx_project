package yy.doctor.frag.meeting;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.util.TextUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.util.CountDown;
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

public class MeetingPPTPicFrag extends BaseMeetingPPTFrag implements OnCompletionListener, OnPreparedListener, CountDown.OnCountDownListener {
    // TODO: 2017/6/6 切换音乐

    private NetworkPhotoView mIvPpt;
    private ImageView mIvDefault;
    private MediaPlayer mMp;
    private CountDown mCountDown;

    private File mFile;
    private boolean mNeedPlay = false;
    private int mRemainTime; // 剩余时间
    private OnPPTPicListener mOnPPTPicListener;

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
        String audioUrl = mDetail.getString(TDetail.audioUrl);

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

        if (!TextUtil.isEmpty(audioUrl)) {
            // 有音频资源

            // 文件夹
            String filePath = CacheUtil.getDownloadCacheDir() + "meetId/" + mMeetId + "/";
            File fileDir = new File(filePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            // 文件
            String type = audioUrl.substring(audioUrl.lastIndexOf(".") + 1);
            String fileName = audioUrl.hashCode() + "." + type;
            mFile = new File(filePath + fileName);
            if (mFile.exists() && mNeedPlay) {
                // 存在播放
                mMp = MediaPlayer.create(getContext(), Uri.fromFile(mFile));
                mMp.setOnCompletionListener(this);
                mMp.setOnPreparedListener(this);
//                if (mMp != null) {
//                    stop();
//                }
                if (mOnPPTPicListener != null) {
                    mOnPPTPicListener.OnAudio(true, mMp.getDuration());
                }
                mMp.prepareAsync();
            } else {
                // 不存在下载
                exeNetworkReq(NetFactory.newDownload(audioUrl, filePath, fileName).build());
            }
        } else {
            // 没有音频资源
            if (mOnPPTPicListener != null) {
                mOnPPTPicListener.OnAudio(false, 0);
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        start();
        mRemainTime = mMp.getDuration() / 1000;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMp.release();
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        if (progress == 100 && mNeedPlay) {
            mMp = MediaPlayer.create(getContext(), Uri.fromFile(mFile));
            mMp.setOnCompletionListener(this);
            mMp.setOnPreparedListener(this);
//            if (mMp != null) {
//                mMp.stop();
//            }
            mMp.prepareAsync();
        }
    }

    @Override
    public void toggle() {
        if (mMp != null) {
            if (mMp.isPlaying()) {
                mMp.pause();
            } else {
                start();
            }
        }
    }

    @Override
    protected void onVisible() {
        mNeedPlay = true;
        if (mMp != null) {
            stop();
            mMp.prepareAsync();
        }
    }

    @Override
    protected void onInvisible() {
        mNeedPlay = false;
        if (mMp != null) {
            stop();
            mMp.release();
            mMp = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNeedPlay = false;
        if (mMp != null) {
            stop();
            mMp.release();
            mMp = null;
        }
    }

    private void start() {
        mMp.start();
        recycle();
        if (mCountDown == null) {
            mCountDown = new CountDown(mRemainTime);
            mCountDown.setListener(this);
        }
        mCountDown.start();
    }

    private void stop() {
        mMp.stop();
        recycle();
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

    public void recycle() {
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }
}
