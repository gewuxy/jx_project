package yy.doctor.view;

import android.content.Context;
import android.util.AttributeSet;

import lib.ys.adapter.PagerAdapterEx;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.fitter.DpFitter;
import lib.ys.view.BannerViewEx;
import yy.doctor.adapter.BannerViewAdapter;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerView extends BannerViewEx<String> {


    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected PagerAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new BannerViewAdapter();
    }

    @Override
    protected int getIndicatorSpace() {
        return DpFitter.dp(3);
    }
}
