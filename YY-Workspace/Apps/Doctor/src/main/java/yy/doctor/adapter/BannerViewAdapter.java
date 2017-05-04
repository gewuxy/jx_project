package yy.doctor.adapter;

import lib.ys.adapter.PagerAdapterEx;
import yy.doctor.adapter.VH.BannerViewVH;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerViewAdapter extends PagerAdapterEx<String,BannerViewVH> {


    @Override
    public int getIndicatorResId(int index) {
        return 0;
    }

    @Override
    public int getConvertViewResId() {
        return 0;
    }

    @Override
    protected void refreshView(int position, BannerViewVH holder) {

    }

}
