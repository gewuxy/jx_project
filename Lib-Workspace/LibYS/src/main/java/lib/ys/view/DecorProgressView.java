package lib.ys.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import lib.ys.R;
import lib.ys.decor.IDecorProgressView;

/**
 * 其实是loading view
 *
 * @author yuansui
 */
public class DecorProgressView extends ImageView implements IDecorProgressView {

    private Animation mAnimRotate;

    public DecorProgressView(Context context, AttributeSet attrs) {
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
}
