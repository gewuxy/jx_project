package yy.doctor.activity.register;

import android.graphics.Color;

import org.json.JSONException;

import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.ys.util.UIUtil;
import lib.ys.view.SideBar;
import lib.yy.activity.base.BaseSRGroupListActivity;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.util.Util;

/**
 * 选择医院的界面
 *
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseSRGroupListActivity<GroupHospital> {

    private SideBar mSideBar;
    private static final int KLetterColor = Color.parseColor("#888888");
    private int KLetterSize;

    @Override
    public void initData() {

        //模拟数据
        GroupHospital groupHospital = new GroupHospital();
        for (int i = 0; i < 10; i++) {
            Hospital hospital = new Hospital();
            groupHospital.add(hospital);
        }

        addItem(groupHospital);
        addItem(groupHospital);
        addItem(groupHospital);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_hospital;
    }

    @Override
    public void initNavBar() {
        Util.addBackIcon(getNavBar(), "医院", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mSideBar = (SideBar) getDecorView().findViewById(R.id.side_bar);
        initSideBar();
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        expandAllGroup();
    }

    /**
     * 初始化SideBar
     */
    private void initSideBar() {
        KLetterSize = UIUtil.dpToPx(10.0F, HospitalActivity.this);
        mSideBar.setTextSize(KLetterSize);
        mSideBar.setPaintColor(KLetterColor);
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
    public boolean canAutoRefresh() {
        if (BuildConfig.TEST) {
            return false;
        } else {
            return true;
        }
    }
}
