package jx.csp.ui.activity.record;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.ui.activity.record.BaseRecordActivity.FragType;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CornerRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.ui.frag.base.BaseFrag;

/**
 * @author CaiXiang
 * @since 2017/9/30
 */
@Route
public class RecordImgFrag extends BaseFrag {

    private NetworkImageView mIv;
    private ImageView mIvAudio;
    private View mLayoutAudio;
    private AnimationDrawable mAnimation;

    private boolean mAnimationState = false; // 动画状态
    private onMediaPlayerListener mListener;

    // 录播时，判断音频文件是否已经存在
    @Arg(opt = true)
    String mAudioFilePath;

    @Arg(opt = true)
    String mUrl;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_record_img;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mIv = findView(R.id.frag_record_iv);
        mIvAudio = findView(R.id.frag_record_iv_audio);
        mLayoutAudio = findView(R.id.frag_record_audio_layout);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.frag_record_audio_layout);
        mIv.placeHolder(R.drawable.ic_default_record)
                .renderer(new CornerRenderer(fitDp(5)))
                .url(mUrl)
                .load();
        mAnimation = (AnimationDrawable) mIvAudio.getBackground();
        //判断音频文件是否存在
        if (TextUtil.isNotEmpty(mAudioFilePath)) {
            File file = new File(mAudioFilePath);
            YSLog.d(TAG, "exist = "  + mAudioFilePath + "   " + file.exists());
            if (file.exists()) {
                showLayoutAudio();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.frag_record_audio_layout) {
            if (mAnimationState) {
                mAnimation.stop();
                mListener.stopPlay();
            } else {
                mAnimation.start();
                mListener.startPlay(mAudioFilePath, this);
            }
            mAnimationState = !mAnimationState;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onInvisible();
    }

    @Override
    protected void onInvisible() {
        if (mAnimationState) {
            mAnimationState = !mAnimationState;
            mAnimation.stop();
        }
    }

    public void showLayoutAudio() {
        showView(mLayoutAudio);
    }

    public void goneLayoutAudio() {
        goneView(mLayoutAudio);
    }

    public void stopAnimation() {
        mAnimationState = !mAnimationState;
        mAnimation.stop();
    }

    public int  getFragType() {
        return FragType.img;
    }

    public interface onMediaPlayerListener {
        void startPlay(String filePath, RecordImgFrag frag);
        void stopPlay();
    }

    public void setPlayerListener(onMediaPlayerListener l) {
        mListener = l;
    }

}
