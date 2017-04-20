package yy.doctor.activity.register;

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

/**
 * 选择医院的界面
 *
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseSRGroupListActivity<GroupHospital> {

    private SideBar mSideBar;

    @Override
    public int getContentViewId() {
        return R.layout.activity_hospital;
    }

    @Override
    public void findViews() {
        super.findViews();

        mSideBar = (SideBar) getDecorView().findViewById(R.id.side_bar);
    }

    @Override
    public void initData() {
        mSideBar.setSingleHeight(UIUtil.dpToPx(10.0F,HospitalActivity.this));
        mSideBar.setPaintColor(R.color.text_888);
        GroupHospital g = new GroupHospital();
        for (int i = 0; i < 10; i++) {
            Hospital h = new Hospital();
            g.add(h);
        }

        addItem(g);
    }

    @Override
    public void initTitleBar() {
//        Util.addBackIcon(getTitleBar(), 0, this);
    }

    @Override
    public MultiGroupAdapterEx<GroupHospital, ? extends ViewHolderEx> createAdapter() {
        return new HospitalAdapter();
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        expandAllGroup();
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
