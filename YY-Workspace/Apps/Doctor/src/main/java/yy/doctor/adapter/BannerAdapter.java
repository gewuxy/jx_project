package yy.doctor.adapter;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.List;

import lib.ys.adapter.PagerAdapterEx;
import yy.doctor.R;
import yy.doctor.activity.BannerActivity;
import yy.doctor.adapter.VH.BannerVH;
import yy.doctor.model.home.Banner;
import yy.doctor.model.home.Banner.TBanner;

/**
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

        List<Banner> list = getData();
        holder.getIv().placeHolder(R.mipmap.ic_default_banner).load();
        holder.getIv().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                BannerActivity.nav(getContext(), list.get(position).getString(TBanner.link), list.get(position).getString(TBanner.title));
            }
        });

    }

}
