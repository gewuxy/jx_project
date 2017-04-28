package yy.doctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Arrays;
import java.util.List;

import yy.doctor.R;
import yy.doctor.activity.me.ProvinceCityData;
import yy.doctor.adapter.VH.ProvinceVH;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ProvinceAdapter extends BaseAdapter {

    private List<String> mList;
    private Context mConttext;
    private ProvinceVH mProvinceVH;
    private int mSelectedItem = 0;

    public ProvinceAdapter(Context mConttext) {
        this.mList = Arrays.asList(ProvinceCityData.KPROVINCES);
        this.mConttext = mConttext;
    }

    public void setSelectItem(int selectItem) {
        this.mSelectedItem = selectItem;
        notifyDataSetChanged();
    }

    public String getProvince(int position){
        return mList.get(position);
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

            convertView = LayoutInflater.from(mConttext).inflate(R.layout.layout_province_item, parent, false);
            mProvinceVH = new ProvinceVH(convertView);
            convertView.setTag(mProvinceVH);

        } else {

            mProvinceVH = (ProvinceVH) convertView.getTag();
        }

        mProvinceVH.getTvProvince().setText(mList.get(position));

        if (mSelectedItem == position) {
            mProvinceVH.getTvProvince().setSelected(true);
            mProvinceVH.getV().setVisibility(View.VISIBLE);
        } else {
            mProvinceVH.getTvProvince().setSelected(false);
            mProvinceVH.getV().setVisibility(View.GONE);

        }

        return convertView;
    }


}
