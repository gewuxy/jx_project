package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

import lib.network.model.NetworkResponse;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.ListResponse;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.CityAdapter;
import yy.doctor.adapter.ProvinceAdapter;
import yy.doctor.model.City;
import yy.doctor.model.Province;
import yy.doctor.model.Province.TProvince;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 省市选择
 *
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ProvinceCityActivity extends BaseActivity {

    private ListView mListProvince;
    private ListView mListCity;

    private ProvinceAdapter mProvinceAdapter;
    private CityAdapter mCityAdapter;

    private List<Province> mProvince;

    private int mListProvincePosition = 0;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_province_city;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "省市", this);

    }

    @Override
    public void findViews() {

        mListProvince = findView(R.id.province_list);
        mListCity = findView(R.id.city_list);

    }

    @Override
    public void setViews() {

        refresh(RefreshWay.dialog);
        exeNetworkRequest(0, NetFactory.province());

        exeNetworkRequest(1, NetFactory.city("110000"));

        mListProvince.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mListProvincePosition = position;
                mProvinceAdapter.setSelectItem(position);
                LogMgr.d(TAG, mProvince.get(position).getString(TProvince.id)+"--------"+position);
                exeNetworkRequest(1, NetFactory.city(mProvince.get(position).getString(TProvince.id)));
                //mCityAdapter.setmList(position);
            }
        });

        mListCity.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                String str = mProvinceAdapter.getProvince(mListProvincePosition) + "-" + mCityAdapter.getCity(position);
                intent.putExtra(Extra.KData, str);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {

        if (id == 0) {

            return JsonParser.evs(nr.getText(), Province.class);
        } else if (id == 1) {

            return JsonParser.evs(nr.getText(), City.class);
        }

        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        stopRefresh();
        if (id == 0) {

            ListResponse<Province> r = (ListResponse<Province>) result;

            mProvince = r.getData();
            mProvinceAdapter = new ProvinceAdapter(this, r.getData());
            mListProvince.setAdapter(mProvinceAdapter);

        } else if (id == 1) {

            ListResponse<City> r = (ListResponse<City>) result;

            if (mCityAdapter == null) {
                mCityAdapter = new CityAdapter(this, r.getData());
                mListCity.setAdapter(mCityAdapter);
            } else {
                mCityAdapter.setmList(r.getData());
            }

        }
    }

}
