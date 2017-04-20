package lib.ys.view.pager.transformer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * PS: 所有都是对于v本身的处理, 所以特别注意translate的话应该是计算增量, 默认已经算完距离了
 *
 * @author yuansui
 */
abstract public class BaseTransformer implements PageTransformer {

    @Override
    public final void transformPage(View page, float position) {
        if (position < -1) {
            onLeft(page, position);
        } else if (position <= 1) {
            onTurn(page, position);
        } else {
            onRight(page, position);
        }
    }

    /**
     * This page is way off-screen to the left
     * [-Infinity,-1)
     */
    abstract protected void onLeft(View v, float position);

    /**
     * a页滑动至b页 ； a页[0, -1]；b页[1, 0]
     * [-1,1]
     */
    abstract protected void onTurn(View v, float position);

    /**
     * This page is way off-screen to the right
     * (1,+Infinity]
     */
    abstract protected void onRight(View v, float position);
}
