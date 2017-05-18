package yy.doctor.view;

import android.content.Context;
import android.util.AttributeSet;

import lib.ys.adapter.PagerAdapterEx;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.fitter.DpFitter;
import lib.ys.view.BannerViewEx;
import yy.doctor.adapter.BannerAdapter;
import yy.doctor.model.home.Banner;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerView extends BannerViewEx<Banner> {

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected PagerAdapterEx<Banner, ? extends ViewHolderEx> createAdapter() {
        return new BannerAdapter();
    }

    @Override
    protected int getIndicatorSpace() {
        return DpFitter.dp(3);
    }

}
