package jx.csp.presenter;

import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import jx.csp.contact.VPEffectContract;
import jx.csp.contact.VPEffectContract.V;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther yuansui
 * @since 2017/11/1
 */

public class VPEffectPresenterImpl extends BasePresenterImpl<VPEffectContract.V> implements VPEffectContract.P {

    private final int KOne = 1;
    private float mLastOffset;
    private float mScale;

    public VPEffectPresenterImpl(V v) {
        super(v);
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    @Override
    public void onPageScrolled(FragmentPagerAdapter adapter, int position, float positionOffset, int itemCount) {
        int realPosition;
        float realOffset;
        int nextPosition;
        if (mLastOffset > positionOffset) {
            realPosition = position + KOne;
            nextPosition = position;
            realOffset = KOne - positionOffset;
        } else {
            realPosition = position;
            nextPosition = position + KOne;
            realOffset = positionOffset;
        }

        if (nextPosition > itemCount - KOne || realPosition > itemCount - KOne) {
            return;
        }

        changeView(adapter.getItem(realPosition).getView(), KOne - realOffset);
        changeView(adapter.getItem(nextPosition).getView(), realOffset);

        mLastOffset = positionOffset;
    }


    /**
     * 改变view的大小  缩放
     */
    private void changeView(View v, float offset) {
        if (v == null) {
            return;
        }
        float scale = KOne + mScale * offset;
        v.setScaleX(scale);
        v.setScaleY(scale);
    }
}
