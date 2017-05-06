package lib.ys.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;


/**
 * View的属性动画工具
 *
 * @author yuansui
 */
public class AnimateUtil {
    public static final long KDuration = 500;

    public static final int KMaxAlpha = 1;
    public static final int KMinAlpha = 0;

    public static void fadeOut(View v) {
        fadeOut(v, KDuration);
    }

    public static void fadeOut(View v, long duration) {
        setAlpha(v, KMinAlpha, duration);
    }

    public static void fadeIn(View v) {
        fadeIn(v, KDuration);
    }

    public static void fadeIn(View v, long duration) {
        setAlpha(v, KMaxAlpha, duration);
    }

    public static void rotate(View v, int degree, long duration) {
        v.animate().setDuration(duration).rotation(degree);
    }

    /**
     * 设置透明度, 0 - 1
     *
     * @param v
     * @param alpha {@link #KMinAlpha} - {@link #KMaxAlpha}
     */
    public static void setAlpha(View v, int alpha) {
        setAlpha(v, alpha, 0);
    }

    public static void setAlpha(View v, int alpha, long duration) {
        v.animate().setDuration(duration).alpha(alpha);
    }

    public static void scaleX(View v, float scaleX, long duration) {
        v.animate().setDuration(duration).scaleX(scaleX);
    }

    /**
     * 增加v的宽度
     *
     * @param v
     * @param destWidth 目标宽度
     * @param duration
     */
    public static void width(View v, int destWidth, long duration) {
        WidthWrapper widthWrapper = new WidthWrapper(v);
        ObjectAnimator.ofInt(widthWrapper, "width", destWidth).setDuration(duration).start();
    }

    public static class WidthWrapper {
        private View mTargetView;

        public WidthWrapper(View v) {
            mTargetView = v;
        }

        public int getWidth() {
            return mTargetView.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTargetView.getLayoutParams().width = width;
            mTargetView.requestLayout();
        }
    }

    public static ValueAnimator getValueNumber(int fromNumber, int toNumber) {
        return getValueNumber(fromNumber, toNumber, KDuration);
    }

    public static ValueAnimator getValueNumber(int fromNumber, int toNumber, long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(fromNumber, toNumber);
        valueAnimator.setDuration(duration);
        return valueAnimator;
    }

    public static ValueAnimator ofFloatValue(float from, float to, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(duration);
        return animator;
    }

    /*********************************
     * 设置位移
     */

    /**
     * @param v
     * @param x
     * @param y
     */
    public static void translate(View v, float x, float y) {
        translate(v, 1.0f, x, y, KDuration);
    }

    public static void translate(View v, float x, float y, long duration) {
        translate(v, 1.0f, x, y, duration);
    }

    public static ViewPropertyAnimator getTranslate(View v, float scale, float x, float y, long duration) {
        ViewPropertyAnimator animator = v.animate();
        animator.setDuration(duration);
        animator.scaleX(scale);
        animator.scaleY(scale);
        float realX = x - (1.0f - scale) * v.getWidth() / 2;
        float realY = y - (1.0f - scale) * v.getHeight() / 2;
        animator.x(realX);
        animator.y(realY);

        return animator;
    }

    public static void translate(View v, float scale, float x, float y, long duration) {
        getTranslate(v, scale, x, y, duration).start();
    }

    public static void setXY(View v, float x, float y) {
        translate(v, x, y, 0);
    }

    public static void translationYBy(View v, float y) {
        ViewPropertyAnimator animator = v.animate();
        animator.setDuration(KDuration);
        animator.yBy(y);
        animator.start();
    }

    public static void translationYBy(View v, float y, int duration) {
        ViewPropertyAnimator animator = v.animate();
        animator.setDuration(duration);
        animator.yBy(y);
        animator.start();
    }

    public static void translationY(View v, float y) {
        ViewPropertyAnimator animator = v.animate();
        animator.setDuration(KDuration);
        animator.y(y);
        animator.start();
    }

    /**
     * 设置一次性监听, 动画完毕后会取消
     *
     * @param v
     * @param l
     */
    public static void setListener(View v, final AnimatorListenerAdapter l) {
        ViewPropertyAnimator animator = v.animate();
        animator.setListener(l);
    }

    public static AnimatorSet playTogether(Animator... animators) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.start();
        return set;
    }
}
