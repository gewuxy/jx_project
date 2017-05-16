package yy.doctor.adapter;

import lib.ys.adapter.PagerAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.BannerViewVH;

/**
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerViewAdapter extends PagerAdapterEx<String, BannerViewVH> {


    @Override
    public int getIndicatorResId(int index) {
        return R.id.home_header_indicator;
    }

    @Override
    public int getConvertViewResId() {
        return R.id.home_header_scroll_view;
    }

    @Override
    protected void refreshView(int position, BannerViewVH holder) {

    }

}
