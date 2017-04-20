package lib.ys.view.pager.transformer;

import android.view.View;

/**
 * 透明度效果
 */
public class GradientTransformer extends BaseTransformer {

    @Override
    protected void onLeft(View v, float position) {
        v.setAlpha(0);
    }

    @Override
    protected void onTurn(View v, float position) {
        float alpha = 1 - Math.abs(position);
        v.setAlpha(alpha);
    }

    @Override
    protected void onRight(View v, float position) {
        v.setAlpha(0);
    }
}