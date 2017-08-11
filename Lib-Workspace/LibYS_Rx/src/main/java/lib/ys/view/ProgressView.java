package lib.ys.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import lib.ys.R;
import lib.ys.ui.decor.IProgressView;

/**
 * 其实是loading view
 *
 * @author yuansui
 */
public class ProgressView extends ImageView implements IProgressView {

    private Animation mAnimRotate;

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setScaleType(ScaleType.CENTER_INSIDE);
        mAnimRotate = getLoadingAnim();
        mAnimRotate.setDuration(getLoadingDuration());
    }

    @Override
    public void start() {
        startAnimation(mAnimRotate);
    }

    @Override
    public void stop() {
        clearAnimation();
    }

    protected Animation getLoadingAnim() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
    }

    protected long getLoadingDuration() {
        return 500;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        clearAnimation();
    }
}
