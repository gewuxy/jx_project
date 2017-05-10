package yy.doctor.frag;

import android.graphics.Color;
import android.view.View;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.adapter.CityAdapter;
import yy.doctor.model.City;
import yy.doctor.model.City.TCity;
import yy.doctor.network.NetFactory;

/**
 * @auther yuansui
 * @since 2017/5/10
 */

public class CityFrag extends BaseSRListFrag<City, CityAdapter> {

    private OnCityListener mListener;
    private String mProvinceId;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        setBackgroundColor(Color.WHITE);

        enableSRRefresh(false);
        enableAutoLoadMore(false);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkRequest(0, NetFactory.city(mProvinceId));
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mListener != null) {
            mListener.onCitySelected(position, getItem(position).getString(TCity.name));
        }
    }

    @Override
    public boolean enableRefreshWhenInit() {
        return false;
    }

    public void setProvinceId(String id) {
        mProvinceId = id;
        refresh(RefreshWay.embed);
        getDataFromNet();
    }

    public interface OnCityListener {
        void onCitySelected(int position, String name);
    }

    public void setListener(OnCityListener l) {
        mListener = l;
    }
}
