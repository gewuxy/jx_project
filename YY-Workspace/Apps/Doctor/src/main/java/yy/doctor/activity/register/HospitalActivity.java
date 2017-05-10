package yy.doctor.activity.register;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.ys.ui.other.NavBar;
import lib.ys.view.SideBar;
import lib.yy.activity.base.BaseSRGroupListActivity;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.activity.me.ProvinceCityActivity;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.model.hospital.Hospital.THospital;
import yy.doctor.util.Util;

/**
 * 选择医院的界面
 *
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseSRGroupListActivity<GroupHospital> {

    private static final int KLetterColorNormal = Color.parseColor("#888888");
    private static final int KLetterColorFocus = Color.parseColor("#0882e7");

    private SideBar mSideBar;
    private int mLetterSize;
    private TextView mTvLetter;
    private TextView mTvChange;

    @Override
    public void initData() {
        mLetterSize = fitDp(10);

        /*refresh(RefreshWay.dialog);
        exeNetworkRequest(0, NetFactory.hospital("广州市"));*/
        //TODO：解析
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
        initSideBar();
    }

    @Override
    public void setViews() {
        super.setViews();

        expandAllGroup();

        setRefreshEnable(false);

        mTvChange.setOnClickListener(this);
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
    public MultiGroupAdapterEx<GroupHospital, ? extends ViewHolderEx> createAdapter() {
        return new HospitalAdapter();
    }

    @Override
    public void getDataFromNet() {
        //
    }

    @Override
    public IListResponse<GroupHospital> parseNetworkResponse(int id, String text) throws JSONException {
        return null;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.hospital_tv_change:
                startActivity(ProvinceCityActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean canAutoRefresh() {
        if (BuildConfig.TEST) {
            return false;
        } else {
            return true;
        }
    }
}
