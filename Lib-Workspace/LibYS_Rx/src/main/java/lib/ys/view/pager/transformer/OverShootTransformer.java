package lib.ys.view.pager.transformer;

import android.view.View;
import android.view.animation.OvershootInterpolator;

import lib.ys.YSLog;

/**
 * FIXME: 未完成, 因为无法判断方向, 暂时没有好方法
 *
 * @author yuansui
 */
public class OverShootTransformer extends BaseTransformer {

    private OvershootInterpolator mInterpolator;

    @Override
    protected void onLeft(View v, float position) {
    }

    @Override
    protected void onTurn(View v, float position) {
        if (mInterpolator == null) {
            mInterpolator = new OvershootInterpolator();
        }
//
        int pageWidth = v.getWidth();

        if (position < 0) {
            // a
//            v.setTranslationX(x);
//            v.setTranslationX(horzMargin - vertMargin / 2);
//            v.setTranslationX(pageWidth * (position));
//            v.setAlpha(0);
        } else {
            // b
            float base = 1 - position;
            float scaleFactor = mInterpolator.getInterpolation(base);
//            scaleFactor -= position;
            int x = (int) ((int) (scaleFactor * pageWidth) - (base) * pageWidth);
            YSLog.d("www", "onTurn: x = " + x);
            YSLog.d("www", "onTurn: scale = " + scaleFactor);
            YSLog.d("www", "onTurn: dis = " + (scaleFactor * pageWidth));
            YSLog.d("www", "onTurn: position = " + position);
            if (x > 0) {

                v.setTranslationX(-x);
            }
        }

        YSLog.d("www", "onTurn: ======================= = ");
    }

    @Override
    protected void onRight(View v, float position) {
    }
}
