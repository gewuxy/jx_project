package yy.doctor.activity.register;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.view.SideBar;
import lib.yy.activity.base.BaseSRGroupListActivity;
import yy.doctor.BuildConfig;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.me.ProvinceCityActivity;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 选择医院的界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseSRGroupListActivity<GroupHospital, HospitalAdapter> {

    private static final int KLetterColorNormal = Color.parseColor("#888888");
    private static final int KLetterColorFocus = Color.parseColor("#0882e7");

    private SideBar mSideBar;
    private int mLetterSize;
    private TextView mTvLetter;
    private TextView mTvChange;
    private TextView mTvLocation;

    @Override
    public void initData() {
        mLetterSize = fitDp(10);

        //TODO：参照单位号
        //模拟数据
        GroupHospital groupHospital = new GroupHospital();

        for (int i = 0; i < 10; i++) {
            Hospital hospital = new Hospital();
            hospital.put(THospital.name, "" + i);
            groupHospital.add(hospital);
        }

        groupHospital.setTag("A");
        addItem(groupHospital);
        addItem(groupHospital);
        addItem(groupHospital);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_hospital;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "医院", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvLetter = findView(R.id.hospital_tv_letter);
        mSideBar = findView(R.id.hospital_sb);
        mTvChange = findView(R.id.hospital_tv_change);
        mTvLocation = findView(R.id.hospital_tv_location);
        initSideBar();
    }

    @Override
    public void setViews() {
        super.setViews();

        expandAllGroup();

        enableSRRefresh(false);

        mTvChange.setOnClickListener(this);
        refresh(RefreshWay.dialog);
        exeNetworkReq(0, NetFactory.hospital("广州市"));
    }

    /**
     * 初始化SideBar
     */
    private void initSideBar() {
        mSideBar.setTextSize(mLetterSize);
        mSideBar.setColor(KLetterColorNormal);
        mSideBar.setColorFocus(KLetterColorFocus);
        mSideBar.setOnTouchLetterChangeListener((s, isFocus) -> {
            mTvLetter.setText(s);
            mTvLetter.setVisibility(isFocus ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void getDataFromNet() {
        //
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
                String city = data.getStringExtra(Extra.KData);
                mTvLocation.setText(getResources().getString(R.string.hospital_location) + city);
            }
        }
    }

    @Override
    public boolean enableRefreshWhenInit() {
        if (BuildConfig.TEST) {
            return false;
        } else {
            return true;
        }
    }
}
