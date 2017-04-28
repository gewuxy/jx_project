package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.CityAdapter;
import yy.doctor.adapter.ProvinceAdapter;
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

    private int mListProvincePosition = 0;

    @Override
    public void initData() {

        mProvinceAdapter = new ProvinceAdapter(this);
        mCityAdapter = new CityAdapter(this);

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

        mListProvince.setAdapter(mProvinceAdapter);
        mListCity.setAdapter(mCityAdapter);

        mListProvince.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mListProvincePosition = position;
                mProvinceAdapter.setSelectItem(position);
                mCityAdapter.setmList(position);

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


}
