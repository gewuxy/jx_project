package jx.csp.ui.frag.record;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.network.model.interfaces.IResult;
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

    @StringDef({
            AudioType.mp3,
            AudioType.amr
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioType {
        String mp3 = "mp3";
        String amr = "amr";
    }

    private NetworkImageView mIv;
    private ImageView mIvAudio;
    private View mLayoutAudio;
    private AnimationDrawable mAnimation;

    private boolean mAnimationState = false; // 动画状态
    private onMediaPlayerListener mListener;

    @Arg
    String mImgUrl;

    // 录播时，判断音频文件是否已经存在
    @Arg(opt = true)
    String mAudioFilePath;

    @Arg(opt = true)
    String mAudioUrl;

    @Override
    public void initData(Bundle state) {
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
                .url(mImgUrl)
                .load();
        mAnimation = (AnimationDrawable) mIvAudio.getBackground();
        // 判断是否上传过音频，如果上传过再判断音频文件是否还存在，不存在就下载，下载下来的是mp3文件,下载完成显示播放按钮
        if (TextUtil.isNotEmpty(mAudioUrl)) {
            File file = new File(mAudioFilePath);
            YSLog.d(TAG, "audio already upload");
            YSLog.d(TAG, "amr file path = " + mAudioFilePath + "  exist = " + file.exists());
            if (file.exists()) {
                showLayoutAudio();
            } else {
                mAudioFilePath = mAudioFilePath.replace(AudioType.amr, AudioType.mp3);
                YSLog.d(TAG, "mp3 file path = " + mAudioFilePath);
                String filePath = mAudioFilePath.substring(0, mAudioFilePath.lastIndexOf(File.separator));
                String fileName = mAudioFilePath.substring(mAudioFilePath.lastIndexOf(File.separator) + 1);
                YSLog.d(TAG, "file path = " + filePath);
                YSLog.d(TAG, "file name = " + fileName);
                // 判断是否已经下载过
                File f = new File(mAudioFilePath);
                if (f.exists()) {
                    showLayoutAudio();
                    YSLog.d(TAG, "已经下载过MP3文件 " + fileName);
                } else {
                    YSLog.d(TAG, "现在下载MP3文件 " + fileName);
                    exeNetworkReq(MeetingAPI.downloadAudio(filePath, fileName, mAudioUrl).build());
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.frag_record_audio_layout) {
            if (mAnimationState) {
                mAnimation.stop();
                mListener.stopPlay();
                mAnimationState = false;
            } else {
                mAnimation.start();
                mListener.startPlay(mAudioFilePath, this);
                mAnimationState = true;
            }
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        showLayoutAudio();
        super.onNetworkSuccess(id, r);
    }

    @Override
    public void onPause() {
        super.onPause();
        onInvisible();
    }

    @Override
    protected void onInvisible() {
        if (mAnimationState) {
            mAnimationState = false;
            mAnimation.stop();
        }
    }

    public void setAudioFilePath(String filePath) {
        mAudioFilePath = filePath;
    }

    public void showLayoutAudio() {
        showView(mLayoutAudio);
    }

    public void goneLayoutAudio() {
        goneView(mLayoutAudio);
    }

    public void stopAnimation() {
        mAnimationState = false;
        mAnimation.stop();
    }

    public interface onMediaPlayerListener {
        void startPlay(String filePath, RecordImgFrag frag);

        void stopPlay();
    }

    public void setPlayerListener(onMediaPlayerListener l) {
        mListener = l;
    }

}
