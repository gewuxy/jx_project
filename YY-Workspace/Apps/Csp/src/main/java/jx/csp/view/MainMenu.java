package jx.csp.view;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.io.IOException;

import jx.csp.R;
import lib.ys.YSLog;

import static lib.ys.util.view.ViewUtil.hideView;
import static lib.ys.util.view.ViewUtil.showView;

/**
 * @auther : GuoXuan
 * @since : 2018/2/9
 */
public class MainMenu extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "MainMenu";

    public enum Status {
        OPEN, CLOSE
    }

    private final int KAnimTime = 250;
    private Status mCurrentStatus = Status.CLOSE;

    private View mIvSwitch; // 切换按钮
    private View mIvNew; // 新建按钮
    private View mIvScan; // 扫一扫按钮
    private View mIvPlus; // 总按钮

    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;
    private MainMenuClickListener mMainMenuClickListener;

    public MainMenu(@NonNull Context context) {
        this(context, null);
    }

    public MainMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View menu = View.inflate(getContext(), R.layout.layout_main_menu, this);
        mIvSwitch = menu.findViewById(R.id.menu_iv_switch);
        mIvNew = menu.findViewById(R.id.menu_iv_new);
        mIvScan = menu.findViewById(R.id.menu_iv_scan);
        mIvPlus = menu.findViewById(R.id.menu_iv_plus);

        mIvSwitch.setOnClickListener(this);
        mIvNew.setOnClickListener(this);
        mIvScan.setOnClickListener(this);
        mIvPlus.setOnClickListener(this);

        mVibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_iv_switch: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
                mIvSwitch.startAnimation(scaleBigAnim(KAnimTime));
                mIvNew.startAnimation(scaleSmallAnim(KAnimTime));
                mIvScan.startAnimation(scaleSmallAnim(KAnimTime));
                if (mMainMenuClickListener != null) {
                    mMainMenuClickListener.switchClick();
                }
            }
            break;
            case R.id.menu_iv_new: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
                mIvNew.startAnimation(scaleBigAnim(KAnimTime));
                mIvSwitch.startAnimation(scaleSmallAnim(KAnimTime));
                mIvScan.startAnimation(scaleSmallAnim(KAnimTime));
                if (mMainMenuClickListener != null) {
                    mMainMenuClickListener.newClick();
                }
            }
            break;
            case R.id.menu_iv_scan: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
                mIvScan.startAnimation(scaleBigAnim(KAnimTime));
                mIvSwitch.startAnimation(scaleSmallAnim(KAnimTime));
                mIvNew.startAnimation(scaleSmallAnim(KAnimTime));
                if (mMainMenuClickListener != null) {
                    mMainMenuClickListener.scanClick();
                }
            }
            break;
            case R.id.menu_iv_plus: {
                mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
                rotateView(mCurrentStatus);
                mVibrator.vibrate(new long[]{0, 50}, -1);
                try {
                    AssetFileDescriptor afd = getContext().getAssets().openFd("main.mp3");
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    //同步准备
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    YSLog.d(TAG, "msg = " + e.getMessage());
                }
            }
            break;
        }
    }

    public void setMainMenuClickListener(MainMenuClickListener listener) {
        mMainMenuClickListener = listener;
    }

    public interface MainMenuClickListener {
        void switchClick();
        void newClick();
        void scanClick();
    }

    public void releaseMediaPlayer() {
        mMediaPlayer.release();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void rotateView(Status mCurrentStatus) {
        if (mCurrentStatus == Status.OPEN) {
            rotateView(mIvPlus, 0f, 45f, KAnimTime);
            childAnimSet(mIvSwitch);
            childAnimSet(mIvNew);
            childAnimSet(mIvScan);
        } else {
            rotateView(mIvPlus, 45f, 0f, KAnimTime);
            childAnimSet(mIvSwitch);
            childAnimSet(mIvNew);
            childAnimSet(mIvScan);
        }
    }

    /**
     * 按钮的旋转动画
     *
     * @param view
     *
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
        transAnim.setAnimationListener(new Animation.AnimationListener() {
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
