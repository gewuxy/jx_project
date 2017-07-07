package yy.doctor.ui.activity.register;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONException;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.interfaces.IListResult;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.BaseGroupIndexActivity;
import yy.doctor.util.Util;

/**
 * 选择医院的界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseGroupIndexActivity<GroupHospital, Hospital, HospitalAdapter> {

    private TextView mTvChange; // 改变按钮
    private TextView mTvLocation; // 选择的地址
    private String mProvince;
    private String mCity;

    public static void nav(Context context, Place place) {
        Intent i = new Intent(context, HospitalActivity.class)
                .putExtra(Extra.KData, place);
        LaunchUtil.startActivityForResult(context, i, 0);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_hospital;
    }

    @Override
    public void initData() {
        super.initData();
        //默认
        Place place = (Place) getIntent().getSerializableExtra(Extra.KData);
        mProvince = place.getString(TPlace.province);
        mCity = place.getString(TPlace.city);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.hospital, this);
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

        mTvLocation.setText(getString(R.string.hospital_location) + mProvince + " " + mCity);
        setOnClickListener(mTvChange);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.hospital(mCity));
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String hospital = getChild(groupPosition, childPosition).getString(THospital.name);
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
    public IListResult<GroupHospital> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.groupIndex(text, GroupHospital.class, THospital.alpha);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.hospital_tv_change:
                startActivity(ProvinceActivity.class);
                break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.province_finish) {
            Place place = (Place) data;
            String city = place.getString(TPlace.city);
            String str = Util.generatePcd(place.getString(TPlace.province), city, null);
            mTvLocation.setText(getString(R.string.hospital_location) + str);
            exeNetworkReq(NetFactory.hospital(city));
        }
    }

}
