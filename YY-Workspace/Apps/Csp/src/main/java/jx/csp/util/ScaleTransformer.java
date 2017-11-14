package jx.csp.util;

import android.view.View;

import lib.ys.view.pager.transformer.BaseTransformer;

/**
 * @author CaiXiang
 * @since 2017/10/24
 */
public class ScaleTransformer extends BaseTransformer {

    private float mMinScale;
    private float mScale;
    private float mOffset;

    public ScaleTransformer(float scale, float offset) {
        mScale = scale;
        mMinScale = 1 - scale;
        mOffset = offset;
    }


    @Override
    public void transformPage(View page, float position) {
        position += mOffset;
        super.transformPage(page, position);
    }

    @Override
    protected void onLeft(View v, float position) {
        v.setScaleX(mMinScale);
        v.setScaleY(mMinScale);
    }

    @Override
    protected void onTurn(View v, float position) {
        if (position < 0) {
            float scale = 1 + mScale * position;
            v.setScaleX(scale);
            v.setScaleY(scale);
        } else {
            float scale = 1 - mScale * position;
            v.setScaleX(scale);
            v.setScaleY(scale);
        }
    }

    @Override
    protected void onRight(View v, float position) {
        v.setScaleX(mMinScale);
        v.setScaleY(mMinScale);
    }
}
