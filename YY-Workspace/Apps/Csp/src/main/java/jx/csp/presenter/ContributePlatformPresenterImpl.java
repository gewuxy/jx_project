package jx.csp.presenter;

import java.util.List;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.ContributePlatformContract;
import jx.csp.model.Platform;
import jx.csp.model.Platform.TPlatformDetail;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.yy.contract.BasePresenterImpl;

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
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (r.isSucceed()) {
            getView().onFinish();
            App.showToast(R.string.contribute_platform_succeed);
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void clickContributeReq(List<Platform> platformArrayList, Platform platform, String courseId) {
        StringBuffer buffer = new StringBuffer();
        int size = platformArrayList.size();
        for (int i = 0; i < size; ++i) {
            platform = platformArrayList.get(i);
            buffer.append(platform.getString(TPlatformDetail.id));
            if (i != size - 1) {
                buffer.append(",");
            }
        }
        exeNetworkReq(DeliveryAPI.unitNum(String.valueOf(buffer), courseId).build());
    }
}
