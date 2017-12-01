package jx.doctor.view;

import android.content.Context;
import android.util.AttributeSet;

import lib.ys.fitter.Fitter;
import lib.ys.view.BannerViewEx;
import jx.doctor.adapter.home.BannerAdapter;
import jx.doctor.model.home.Banner;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerView extends BannerViewEx<Banner, BannerAdapter> {

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getIndicatorSpace() {
        return Fitter.dp(3);
    }
}
