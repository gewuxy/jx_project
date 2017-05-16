package yy.doctor.activity.register;

import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONException;

import lib.ys.LogMgr;
import lib.ys.network.resp.IListResp;
import lib.ys.ui.other.NavBar;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.BaseGroupIndexActivity;
import yy.doctor.activity.me.ProvinceCityActivity;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 选择医院的界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseGroupIndexActivity<GroupHospital, HospitalAdapter> {

    private TextView mTvChange;// 改变按钮
    private TextView mTvLocation;// 选择的地址
    private String mProvince;
    private String mCity;

    @Override
    public int getContentViewId() {
        return R.layout.activity_hospital;
    }

    @Override
    public void initData() {
        super.initData();
        mProvince = "广东";//默认
        mCity = "广州";
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "医院", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvChange = findView(R.id.hospital_tv_change);
        mTvLocation = findView(R.id.hospital_tv_location);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mTvChange);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(0, NetFactory.hospital("广州市"));
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String hospital = getData().get(groupPosition).getChild(childPosition).getString(THospital.name);
        Intent intent = new Intent()
                .putExtra(Extra.KProvince, mProvince)
                .putExtra(Extra.KCity, mCity)
                .putExtra(Extra.KData, hospital);
        setResult(RESULT_OK, intent);

        finish();
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }

    @Override
    public IListResp<GroupHospital> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.groupIndex(text, GroupHospital.class, THospital.alpha);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.hospital_tv_change:
                startActivityForResult(ProvinceCityActivity.class, 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                mProvince = data.getStringExtra(Extra.KProvince);
                mCity = data.getStringExtra(Extra.KCity);
                mTvLocation.setText(getResources().getString(R.string.hospital_location) + mProvince + " " + mCity);
                exeNetworkReq(0, NetFactory.hospital(mCity));
            }
        }
    }

}
