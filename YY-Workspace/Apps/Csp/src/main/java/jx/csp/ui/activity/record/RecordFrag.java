package jx.csp.ui.activity.record;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import jx.csp.R;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;

/**
 * @author CaiXiang
 * @since 2017/9/30
 */

public class RecordFrag extends BaseFrag {

    private NetworkImageView mIv;
    private ImageView mIvAudio;
    private AnimationDrawable mAnimation;

    private boolean mAnimationState = false; // 动画状态

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_record;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mIv = findView(R.id.frag_record_iv);
        mIvAudio = findView(R.id.frag_record_iv_audio);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.frag_record_audio_layout);
        mIv.placeHolder(R.drawable.ic_default_record).load();
        mAnimation = (AnimationDrawable) mIvAudio.getBackground();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.frag_record_audio_layout) {
            if (mAnimationState) {
                mAnimation.stop();
            } else {
                mAnimation.start();
            }
            mAnimationState = !mAnimationState;
        }
    }

    @Override
    protected void onInvisible() {
        if (mAnimationState) {
            mAnimation.stop();
        }
    }

}
