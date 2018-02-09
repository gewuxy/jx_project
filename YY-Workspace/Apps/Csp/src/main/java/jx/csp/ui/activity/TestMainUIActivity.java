package jx.csp.ui.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import jx.csp.R;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ui.other.NavBar;

/**
 * @author CaiXiang
 * @since 2018/2/1
 */

public class TestMainUIActivity extends BaseActivity {

    public enum Status {
        OPEN, CLOSE
    }

    private ImageView mIvPlus;
    private ImageView mIvKind;
    private ImageView mIvNew;
    private ImageView mIvScan;

    private Status mCurrentStatus = Status.CLOSE;
    private final int KAnimTime = 250;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_test_main_ui;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addTextViewMid("ArcMenu");
    }

    @Override
    public void findViews() {
        mIvPlus = findView(R.id.menu_iv_plus);
        mIvKind = findView(R.id.menu_iv_switch);
        mIvNew = findView(R.id.main_iv_new);
        mIvScan = findView(R.id.menu_iv_scan);
    }

    @Override
    public void setViews() {
        setOnClickListener(mIvPlus);
        setOnClickListener(mIvKind);
        setOnClickListener(mIvNew);
        setOnClickListener(mIvScan);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_iv_plus: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
            }
            break;
            case R.id.menu_iv_switch: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
                mIvKind.startAnimation(scaleBigAnim(KAnimTime));
                mIvNew.startAnimation(scaleSmallAnim(KAnimTime));
                mIvScan.startAnimation(scaleSmallAnim(KAnimTime));
            }
            break;
            case R.id.main_iv_new: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
                mIvNew.startAnimation(scaleBigAnim(KAnimTime));
                mIvKind.startAnimation(scaleSmallAnim(KAnimTime));
                mIvScan.startAnimation(scaleSmallAnim(KAnimTime));
            }
            break;
            case R.id.menu_iv_scan: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
                mIvScan.startAnimation(scaleBigAnim(KAnimTime));
                mIvKind.startAnimation(scaleSmallAnim(KAnimTime));
                mIvNew.startAnimation(scaleSmallAnim(KAnimTime));
            }
            break;
        }
    }

    public void rotateView(Status mCurrentStatus) {
        if (mCurrentStatus == Status.OPEN) {
            rotateView(mIvPlus, 0f, 45f, KAnimTime);
            childAnimSet(mIvKind);
            childAnimSet(mIvNew);
            childAnimSet(mIvScan);
        } else {
            rotateView(mIvPlus, 45f, 0f, KAnimTime);
            childAnimSet(mIvKind);
            childAnimSet(mIvNew);
            childAnimSet(mIvScan);
        }
    }

    /**
     * 按钮的旋转动画
     *
     * @param view
     * @param fromDegrees
     * @param toDegrees
     * @param durationMillis
     */
    private void rotateView(View view, float fromDegrees, float toDegrees, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(rotate);
    }

    private void childAnimSet(View view) {
        showView(view);
        AnimationSet animSet = new AnimationSet(true);
        Animation transAnim = null;
        if (mCurrentStatus == Status.OPEN) {
            // to open
            float fromX = mIvPlus.getX() + mIvPlus.getWidth() / 2 - view.getWidth() / 2 - view.getX();
            float fromY = mIvPlus.getY() + mIvPlus.getHeight() / 2 - view.getHeight() / 2 - view.getY();
            transAnim = new TranslateAnimation(fromX, 0, fromY, 0);
            view.setClickable(true);
        } else {
            // to close
            float toX = mIvPlus.getX() + mIvPlus.getWidth() / 2 - view.getWidth() / 2 - view.getX();
            float toY = mIvPlus.getY() + mIvPlus.getHeight() / 2 - view.getHeight() / 2 - view.getY();
            transAnim = new TranslateAnimation(0, toX, 0, toY);
            view.setClickable(false);
        }

        transAnim.setFillAfter(true);
        transAnim.setDuration(KAnimTime);
        transAnim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (mCurrentStatus == Status.CLOSE) {
                    hideView(view);
                }
            }
        });
        RotateAnimation rotateAnim = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(KAnimTime);
        rotateAnim.setFillAfter(true);
        animSet.addAnimation(rotateAnim);
        animSet.addAnimation(transAnim);
        view.startAnimation(animSet);
    }

    /**
     * 放大，透明度降低
     */
    private Animation scaleBigAnim(int durationMillis) {
        AnimationSet animationset = new AnimationSet(true);
        Animation anim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        animationset.addAnimation(anim);
        animationset.addAnimation(alphaAnimation);
        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);
        return animationset;
    }

    /**
     * 缩小消失
     */
    private Animation scaleSmallAnim(int durationMillis) {
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
    }

}
