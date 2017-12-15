package jx.csp.presenter;

import jx.csp.contact.VipManageContract;
import jx.csp.contact.VipManageContract.V;
import jx.csp.model.VipPackage;
import jx.csp.model.VipPackage.TPackage;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.VipAPI;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;

/**
 * @auther Huoxuyu
 * @since 2017/12/12
 */

public class VipManagePresenterImpl extends BasePresenterImpl<VipManageContract.V> implements VipManageContract.P {

    public VipManagePresenterImpl(V v) {
        super(v);
    }

    @Override
    public void checkPackage() {
        exeNetworkReq(VipAPI.vip().build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), VipPackage.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            VipPackage vipPackage = (VipPackage) r.getData();
            int packageId = vipPackage.getInt(TPackage.id);
            long packageStart = vipPackage.getLong(TPackage.packageStart);
            long packageEnd = vipPackage.getLong(TPackage.packageEnd);
            int packageMeetCount = vipPackage.getInt(TPackage.usedMeetCount);
            getView().setAdapterData(packageId, packageStart, packageEnd, packageMeetCount);
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
