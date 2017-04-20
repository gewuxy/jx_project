package lib.ys.view.pager.transformer;

import android.view.View;

/**
 * 官方放大效果
 */
public class ZoomOutTransformer extends BaseTransformer {
    private static final float KMinScale = 0.85f;
    private static final float KMinAlpha = 0.5f;

    @Override
    protected void onLeft(View v, float position) {
        v.setAlpha(0);
    }

    @Override
    protected void onTurn(View v, float position) {
        float scaleFactor = Math.max(KMinScale, 1 - Math.abs(position));

        int pageWidth = v.getWidth();
        int pageHeight = v.getHeight();

        float vertMargin = pageHeight * (1 - scaleFactor) / 2;
        float horzMargin = pageWidth * (1 - scaleFactor) / 2;
        if (position < 0) {
            v.setTranslationX(horzMargin - vertMargin / 2);
        } else {
            v.setTranslationX(-horzMargin + vertMargin / 2);
        }

        // Scale the page down (between KMinScale and 1)
        v.setScaleX(scaleFactor);
        v.setScaleY(scaleFactor);

        // Fade the page relative to its size.
        v.setAlpha(KMinAlpha + (scaleFactor - KMinScale) / (1 - KMinScale) * (1 - KMinAlpha));
    }

    @Override
    protected void onRight(View v, float position) {
        v.setAlpha(0);
    }
}  