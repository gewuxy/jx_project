package jx.csp.presenter;

import java.util.ArrayList;
import java.util.List;

import jx.csp.contact.VipManageContract;
import jx.csp.contact.VipManageContract.V;
import jx.csp.model.VipPackage;
import jx.csp.model.VipPackage.TPackage;
import jx.csp.model.VipPermission;
import jx.csp.model.VipPermission.TVip;
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

    private int mPackageId;
    private long mPackageStart;
    private long mPackageEnd;
    private String mPackageMeetCount;

    public VipManagePresenterImpl(V v) {
        super(v);
    }

    @Override
    public List<VipPermission> addPermission(int[] id, int[] image, String[] text) {
        List<VipPermission> list = new ArrayList<>();
        if (image == null || text == null || id == null) {
            return list;
        }
        int length = Math.min(Math.min(image.length, text.length), id.length);
        for (int i = 0; i < length; i++) {
            VipPermission vip = new VipPermission();
            vip.put(TVip.image, image[i]);
            vip.put(TVip.text, text[i]);
            list.add(vip);
        }
        return list;
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
            mPackageId = vipPackage.getInt(TPackage.id);
            mPackageStart = vipPackage.getLong(TPackage.packageStart);
            mPackageEnd = vipPackage.getLong(TPackage.packageEnd);
            mPackageMeetCount = vipPackage.getString(TPackage.usedMeetCount);
            getView().setAdapterData(mPackageId, mPackageStart, mPackageEnd, mPackageMeetCount);
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
