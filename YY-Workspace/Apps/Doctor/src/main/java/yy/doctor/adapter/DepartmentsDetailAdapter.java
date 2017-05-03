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
import yy.doctor.adapter.VH.DepartmentsDetailVH;

/**
 * @author CaiXiang
 * @since 2017/5/2
 */
public class DepartmentsDetailAdapter extends BaseAdapter {

    private List<String> mList;

    private Context mConttext;
    private DepartmentsDetailVH mDepartmentsDetailVH;

    public DepartmentsDetailAdapter(Context mConttext) {
        this.mList = Arrays.asList(DepartmentsData.KDEPARTMENTSDETAIL[0]);
        this.mConttext = mConttext;
    }

    public void setmList(int position) {
        this.mList = Arrays.asList(DepartmentsData.KDEPARTMENTSDETAIL[position]);
        notifyDataSetChanged();
    }

    public String getDepartmentsDetail(int position) {
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

            convertView = LayoutInflater.from(mConttext).inflate(R.layout.layout_city_item, parent, false);
            mDepartmentsDetailVH = new DepartmentsDetailVH(convertView);
            convertView.setTag(mDepartmentsDetailVH);

        } else {

            mDepartmentsDetailVH = (DepartmentsDetailVH) convertView.getTag();
        }

        mDepartmentsDetailVH.getTvCity().setText(mList.get(position));

        return convertView;
    }

}
