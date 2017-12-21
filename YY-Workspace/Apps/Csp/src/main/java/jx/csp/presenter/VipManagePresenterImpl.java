package jx.csp.presenter;

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.constant.VipType;
import jx.csp.contact.VipManageContract;
import jx.csp.contact.VipManageContract.V;
import jx.csp.model.VipPackage;
import jx.csp.model.VipPackage.TPackage;
import jx.csp.model.VipPermission;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.VipAPI;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.res.ResLoader;

import static lib.ys.util.res.ResLoader.getString;

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
            getView().setViewState(ViewState.normal);

            VipPackage vipPackage = (VipPackage) r.getData();
            if (vipPackage == null) {
                onNetworkError(id, r.getError());
            } else {
                int packageId = vipPackage.getInt(TPackage.id);
                long packageStart = vipPackage.getLong(TPackage.packageStart);
                long packageEnd = vipPackage.getLong(TPackage.packageEnd);
                int packageMeetCount = vipPackage.getInt(TPackage.usedMeetCount);
                getView().setPackageData(packageId, packageStart, packageEnd, packageMeetCount);

                List<VipPermission> ps = getAdapterData(packageId);
                getView().setPermission(ps);
            }
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        getView().setViewState(ViewState.error);
    }

    private List<VipPermission> getAdapterData(int id) {
        List<VipPermission> list = new ArrayList<>();
        switch (id) {
            case VipType.norm: {
                list.add(new VipPermission(getString(R.string.vip_manage_record), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_record));
                list.add(new VipPermission(getString(R.string.vip_manage_live), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_live));
                list.add(new VipPermission(getString(R.string.vip_manage_three_meeting), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_meet_num));
                list.add(new VipPermission(getString(R.string.vip_manage_advertising), ResLoader.getColor(R.color.text_507a8b9f), R.drawable.vip_ic_un_advertising));
                list.add(new VipPermission(getString(R.string.vip_manage_close_watermark), ResLoader.getColor(R.color.text_507a8b9f), R.drawable.vip_ic_un_watermark));
            }
            break;
            case VipType.advanced: {
                list.add(new VipPermission(getString(R.string.vip_manage_record), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_record));
                list.add(new VipPermission(getString(R.string.vip_manage_live), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_live));
                list.add(new VipPermission(getString(R.string.vip_manage_ten_meeting), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_meet_num));
                list.add(new VipPermission(getString(R.string.vip_manage_advertising), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_advertising));
                list.add(new VipPermission(getString(R.string.vip_manage_close_watermark), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_watermark));
            }
            break;
            case VipType.profession: {
                list.add(new VipPermission(getString(R.string.vip_manage_record), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_record));
                list.add(new VipPermission(getString(R.string.vip_manage_live), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_live));
                list.add(new VipPermission(getString(R.string.vip_manage_infinite_meeting), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_meet_num));
                list.add(new VipPermission(getString(R.string.vip_manage_advertising), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_advertising));
                list.add(new VipPermission(getString(R.string.vip_manage_custom_watermark), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_watermark));
            }
            break;
        }
        return list;
    }
}
