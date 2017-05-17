package yy.doctor.view;

import android.content.Context;
import android.util.AttributeSet;

import lib.ys.fitter.DpFitter;
import lib.ys.view.BannerViewEx;
import yy.doctor.adapter.BannerViewAdapter;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerView extends BannerViewEx<String, BannerViewAdapter> {

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getIndicatorSpace() {
        return DpFitter.dp(3);
    }
}
