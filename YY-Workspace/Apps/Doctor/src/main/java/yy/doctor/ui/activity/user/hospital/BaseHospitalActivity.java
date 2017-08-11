package yy.doctor.ui.activity.user.hospital;

import java.util.ArrayList;
import java.util.List;

import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.IHospital;

/**
 * 医院基类
 *
 * @auther yuansui
 * @since 2017/8/11
 */
abstract public class BaseHospitalActivity extends BaseSRListActivity<IHospital, HospitalAdapter> {

    /**
     * 模拟网络成功, 显示empty footer view
     *
     * @param reqId
     */
    protected void simulateSuccess(int reqId) {
        ListResult<IHospital> r = new ListResult<>();
        r.setCode(ErrorCode.KOk);
        List<IHospital> hospitals = new ArrayList<>();
        r.setData(hospitals);
        onNetworkSuccess(reqId, r);
    }
}
