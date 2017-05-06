package yy.doctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import yy.doctor.R;
import yy.doctor.adapter.VH.CityVH;
import yy.doctor.model.City;
import yy.doctor.model.City.TCity;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class CityAdapter extends BaseAdapter {


    private List<City> mList;

    private Context mConttext;
    private CityVH mCityVH;

    public CityAdapter(Context mConttext ,List<City> list) {
        this.mList = list;
        this.mConttext = mConttext;
    }

    public void setmList(List<City> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public String getCity(int position){
        return mList.get(position).getString(TCity.name);
    }

    @Override

    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(mConttext).inflate(R.layout.layout_city_item, parent, false);
            mCityVH = new CityVH(convertView);
            convertView.setTag(mCityVH);

        } else {

            mCityVH = (CityVH) convertView.getTag();
        }

        mCityVH.getTvCity().setText(mList.get(position).getString(TCity.name));

        return convertView;
    }

}
