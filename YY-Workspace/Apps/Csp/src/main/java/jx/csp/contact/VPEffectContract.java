package jx.csp.contact;

import lib.ys.adapter.FragPagerAdapterEx;
import lib.yy.contract.IContract;

/**
 * viewpager滑动效果
 *
 * @auther yuansui
 * @since 2017/11/1
 */
public interface VPEffectContract {
    interface V extends IContract.View {
    }

    interface P extends IContract.Presenter<V> {
        void onPageScrolled(FragPagerAdapterEx adapter, int position, float positionOffset, int itemCount);
    }
}
