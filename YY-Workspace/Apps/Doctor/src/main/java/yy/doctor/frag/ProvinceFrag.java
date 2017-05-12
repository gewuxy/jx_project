package yy.doctor.frag;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.adapter.ProvinceAdapter;
import yy.doctor.model.Province;
import yy.doctor.model.Province.TProvince;
import yy.doctor.network.NetFactory;

/**
 * @auther yuansui
 * @since 2017/5/10
 */

public class ProvinceFrag extends BaseSRListFrag<Province, ProvinceAdapter> {

    private OnProvinceListener mListener;
    private boolean mInit = true;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(fitDp(0));
        enableSRRefresh(false);
        enableAutoLoadMore(false);
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();

        if (mInit) {
            mInit = false;
            onItemClick(null, 0);
        }
    }

    @Override
    public void getDataFromNet() {
        exeNetworkRequest(0, NetFactory.province());
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mListener != null) {
            Province p = getItem(position);
            mListener.onProvinceSelected(position, p.getString(TProvince.id), p.getString(TProvince.name));
        }

        getAdapter().setSelectedPosition(position);
    }

    public interface OnProvinceListener {
        void onProvinceSelected(int position, String id, String name);
    }

    public void setListener(OnProvinceListener l) {
        mListener = l;
    }

}
