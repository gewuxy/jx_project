package jx.csp.ui.frag;

import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import lib.jx.ui.frag.base.BaseFrag;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.shape.CornerRenderer;
import lib.ys.ui.other.NavBar;

/**
 * @author CaiXiang
 * @since 2018/2/5
 */

@Route
public class GreenHandsVideoFrag extends BaseFrag {

    @Arg
    String mVideoUrl;
    @Arg
    String mImgUrl; // 视频第一针图片地址

    private PLVideoTextureView mTextureView;
    private NetworkImageView mIv;
    private ImageView mIvBgDefault;
    private float mCrown; // 图片的宽高比
    private int mIvHeight;  // 图片高度
    private videoPlayListener mPlayListener;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_green_hands_video;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mTextureView = findView(R.id.frag_green_hands_video_texture_view);
        mIvBgDefault = findView(R.id.frag_green_hands_video_iv_bg);
        mIv = findView(R.id.frag_green_hands_video_iv);
    }

    @Override
    public void setViews() {
        mIv.renderer(new CornerRenderer(fit(5)))
                .url(mImgUrl)
                .listener(new NetworkImageListener() {
                    @Override
                    public void onImageSet(ImageInfo info) {
                        int height = info.getHeight();
                        int width = info.getWidth();
                        mCrown = (width * 1.0f) / height;
                        mIvHeight = (int) (332 / mCrown);
                        if (mIvHeight > 246) {
                            mIvHeight = 246;
                        }
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIv.getLayoutParams();
                        params.height = fit(mIvHeight);
                        mIv.setLayoutParams(params);
                        goneView(mIvBgDefault);
                    }
                })
                .resize(fit(332), fit(mIvHeight))
                .load();
    }

    @Override
    protected void onInvisible() {
        if (mTextureView != null && mTextureView.isPlaying()) {
            mTextureView.stopPlayback();
            showView(mIv);
            goneView(mTextureView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTextureView != null && mTextureView.isPlaying()) {
            mTextureView.stopPlayback();
            showView(mIv);
            goneView(mTextureView);
            mTextureView.releaseSurfactexture();
        }
    }

    public void startPlay() {
        goneView(mIv);
        showView(mTextureView);
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
        mTextureView.setAVOptions(options);
        mTextureView.setVideoPath(mVideoUrl);
        mTextureView.start();
        mTextureView.setOnCompletionListener(plMediaPlayer -> {
            mTextureView.stopPlayback();
            mPlayListener.videoPlayCompletion();
        });
        mPlayListener.videoTime((int) (mTextureView.getDuration()/ TimeUnit.SECONDS.toMillis(1)));
    }

    public int getPlayPos() {
        return (int) ((mTextureView.getDuration() - mTextureView.getCurrentPosition())/TimeUnit.SECONDS.toMillis(1));
    }

    public void stopPlay() {
        mTextureView.stopPlayback();
    }

    public interface videoPlayListener {
        void videoPlayCompletion();
        void videoTime(int time); // 单位秒
    }

    public void setVideoPlayListener(videoPlayListener l) {
        mPlayListener = l;
    }
}
