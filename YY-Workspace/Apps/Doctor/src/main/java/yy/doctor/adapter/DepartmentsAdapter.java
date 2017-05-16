package yy.doctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Arrays;
import java.util.List;

import yy.doctor.DepartmentsData;
import yy.doctor.R;
import yy.doctor.adapter.VH.DepartmentsVH;

/**
 * @author CaiXiang
 * @since 2017/5/2
 */
public class DepartmentsAdapter extends BaseAdapter {

    private List<String> mList;
    private Context mContext;
    private DepartmentsVH mDepartmentsVH;
    private int mSelectedItem = 0;

    public DepartmentsAdapter(Context mContext) {
        this.mContext = mContext;
        mList = Arrays.asList(DepartmentsData.KDEPARTMENTS);
    }

    public void setSelectItem(int selectItem) {
        this.mSelectedItem = selectItem;
        notifyDataSetChanged();
    }

    public String getDepartments(int position) {
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

            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_province_item, parent, false);
            mDepartmentsVH = new DepartmentsVH(convertView);
            convertView.setTag(mDepartmentsVH);

        } else {

            mDepartmentsVH = (DepartmentsVH) convertView.getTag();
        }

        mDepartmentsVH.getTvProvince().setText(mList.get(position));

        if (mSelectedItem == position) {
            mDepartmentsVH.getTvProvince().setSelected(true);
            mDepartmentsVH.getV().setVisibility(View.VISIBLE);
        } else {
            mDepartmentsVH.getTvProvince().setSelected(false);
            mDepartmentsVH.getV().setVisibility(View.GONE);
        }

        return convertView;
    }

}
