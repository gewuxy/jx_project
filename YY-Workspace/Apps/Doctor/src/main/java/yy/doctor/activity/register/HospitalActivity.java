package yy.doctor.activity.register;

import org.json.JSONException;

import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.yy.activity.base.BaseGroupListActivity;
import lib.yy.activity.base.BaseSRGroupListActivity;
import yy.doctor.BuildConfig;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;

/**
 * 更多医院的界面
 *
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseSRGroupListActivity<GroupHospital> {

    @Override
    public void initData() {
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
