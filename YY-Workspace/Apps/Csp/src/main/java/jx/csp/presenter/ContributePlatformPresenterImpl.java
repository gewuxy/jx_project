package jx.csp.presenter;

import android.util.Log;

import java.util.ArrayList;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.ContributePlatformContract;
import jx.csp.model.Platform;
import jx.csp.model.Platform.TPlatformDetail;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import lib.network.model.NetworkResp;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public class ContributePlatformPresenterImpl extends BasePresenterImpl<ContributePlatformContract.V>
        implements ContributePlatformContract.P {

    public ContributePlatformPresenterImpl(ContributePlatformContract.V v) {
        super(v);
    }

    @Override
    public void clickContributeReq(ArrayList<Platform> platformArrayList, Platform platform) {
        StringBuffer buffer = new StringBuffer();
        int size = platformArrayList.size();
        for (int i = 0; i < size; ++i) {
            platform = platformArrayList.get(i);
            buffer.append(platform.getString(TPlatformDetail.id));
            if (i != size - 1) {
                buffer.append(",");
            }
        }
        Log.d(TAG, " buffer " + buffer);
        exeNetworkReq(DeliveryAPI.unitNum(String.valueOf(buffer), platform.getInt(TPlatformDetail.id)).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            getView().stopRefreshItem();
            App.showToast(R.string.contribute_platform_succeed);
        } else {
            onNetworkError(id, r.getError());
            App.showToast(r.getMessage());
        }
    }
}
