package jx.doctor.adapter.home;

import lib.ys.adapter.PagerAdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.home.BannerVH;
import jx.doctor.model.home.Banner;
import jx.doctor.model.home.Banner.TBanner;
import jx.doctor.ui.activity.home.BannerActivityRouter;

/**
 * Banner的adapter
 *
 * @author CaiXiang
 * @since 2017/5/4
 */
public class BannerAdapter extends PagerAdapterEx<Banner, BannerVH> {

    @Override
    public int getIndicatorResId(int index) {
        return R.drawable.home_banner_dot_selector;
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_banner_view_item;
    }

    @Override
    protected void refreshView(int position, BannerVH holder) {
        Banner item = getItem(position);
        holder.getIv().placeHolder(R.drawable.ic_default_banner)
                .url(item.getString(TBanner.pageUrl))
                .load();

        holder.getLayoutRoot().setOnClickListener(v ->
                BannerActivityRouter.create(
                        item.getString(TBanner.link),
                        item.getString(TBanner.title)
                )
                        .route(getContext()));
    }

    @Override
    public boolean isLoop() {
        return true;
    }
}
