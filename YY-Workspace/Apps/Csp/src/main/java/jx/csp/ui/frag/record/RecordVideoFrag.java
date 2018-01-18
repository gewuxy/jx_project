package jx.csp.ui.frag.record;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import lib.jx.ui.frag.base.BaseFrag;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.shape.CornerRenderer;
import lib.ys.ui.other.NavBar;

/**
 * @author CaiXiang
 * @since 2017/10/16
 */
@Route
public class RecordVideoFrag extends BaseFrag {

    private PLVideoTextureView mTextureView;
    private ImageView mIvPlay;
    private NetworkImageView mIvBg;
    private View mVideoStop;

    @Arg
    String mVideoUrl;   // http://139.199.170.178/course/14078/video/17062416023914941915.mp4
    @Arg
    String mImgUrl; // 视频第一针图片地址

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_record_video;
    }

    @Override
    public void initNavBar(NavBar bar) {}

    @Override
    public void findViews() {
        mTextureView = findView(R.id.frag_record_video_texture_view);
        mIvPlay = findView(R.id.frag_record_video_iv_play);
        mIvBg = findView(R.id.frag_record_video_iv_bg);
        mVideoStop = findView(R.id.frag_record_video_stop);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.frag_record_video_iv_play);
        setOnClickListener(R.id.frag_record_video_stop);
        mIvBg.placeHolder(R.drawable.ic_default_record)
                .renderer(new CornerRenderer(fit(5)))
                .url(mImgUrl)
                .resize(fit(332), fit(246))
                .load();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_record_video_iv_play: {
                goneView(mIvPlay);
                goneView(mIvBg);
                showView(mTextureView);
                showView(mVideoStop);
                AVOptions options = new AVOptions();
                options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
                mTextureView.setAVOptions(options);
                mTextureView.setVideoPath(mVideoUrl);
                mTextureView.start();
                mTextureView.setOnCompletionListener(plMediaPlayer -> {
                    mTextureView.stopPlayback();
                    showView(mIvPlay);
                    showView(mIvBg);
                    goneView(mTextureView);
                });
            }
            break;
            case R.id.frag_record_video_stop: {
                YSLog.d(TAG, "stopPlay");
                mTextureView.stopPlayback();
                showView(mIvPlay);
                showView(mIvBg);
                goneView(mTextureView);
                goneView(mVideoStop);
            }
            break;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        onInvisible();
    }

    @Override
    protected void onInvisible() {
        if (mTextureView != null && mTextureView.isPlaying()) {
            mTextureView.stopPlayback();
            showView(mIvPlay);
            showView(mIvBg);
            goneView(mTextureView);
            goneView(mVideoStop);
        }
    }
}
