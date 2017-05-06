package lib.ys.view.pager.transformer;

import android.view.View;

/**
 * 网上的旋转例子
 */
public class RotateDownTransformer extends BaseTransformer {

    private static final float KRotMax = 20.0f;
    private float mRot;

    @Override
    protected void onLeft(View v, float position) {
        v.setRotation(0);
    }

    @Override
    protected void onTurn(View v, float position) {
        if (position < 0) {
            mRot = (KRotMax * position);
            v.setPivotX(v.getMeasuredWidth() * 0.5f);
            v.setPivotY(v.getMeasuredHeight());
            v.setRotation(mRot);
        } else {
            mRot = (KRotMax * position);
            v.setPivotX(v.getMeasuredWidth() * 0.5f);
            v.setPivotY(v.getMeasuredHeight());
            v.setRotation(mRot);
        }
    }

    @Override
    protected void onRight(View v, float position) {
        v.setRotation(0);
    }
}  