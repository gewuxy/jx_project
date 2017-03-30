package lib.ys.view.pager.transformer;

import android.view.View;

/**
 * 官方层叠效果
 */
public class StackTransformer extends BaseTransformer {
    private static final float KMinScale = 0.75f;

    @Override
    protected void onLeft(View v, float position) {
        v.setAlpha(0);
    }

    @Override
    protected void onTurn(View v, float position) {
        if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            v.setAlpha(1);
            v.setTranslationX(0);
            v.setScaleX(1);
            v.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            v.setAlpha(1 - position);

            int pageWidth = v.getWidth();
            // Counteract the default slide transition
            v.setTranslationX(pageWidth * -position);

            // Scale the page down (between KMinScale and 1)
            float scaleFactor = KMinScale + (1 - KMinScale) * (1 - Math.abs(position));
            v.setScaleX(scaleFactor);
            v.setScaleY(scaleFactor);
        }
    }

    @Override
    protected void onRight(View v, float position) {
        v.setAlpha(0);
    }
}  