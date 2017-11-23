package jx.csp.presenter;

import java.util.ArrayList;
import java.util.List;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.ContributePlatformContract;
import jx.csp.model.Platform;
import jx.csp.model.Platform.TPlatformDetail;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import lib.network.model.interfaces.IResult;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public class ContributePlatformPresenterImpl extends BasePresenterImpl<ContributePlatformContract.V>
        implements ContributePlatformContract.P {

    private List<Platform> mSelectedItem;

    public ContributePlatformPresenterImpl(ContributePlatformContract.V v) {
        super(v);

        mSelectedItem = new ArrayList<>();
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
    public void addItem(Platform position, boolean isSelected) {
        if (isSelected) {
            mSelectedItem.add(position);
        } else {
            mSelectedItem.remove(position);
        }
        getView().changeButtonStatus(mSelectedItem.isEmpty());
    }

    @Override
    public void clickContribute(String courseId) {
        StringBuffer buffer = new StringBuffer();
        int size = mSelectedItem.size();
        Platform platform = null;
        for (int i = 0; i < size; ++i) {
            platform = mSelectedItem.get(i);
            buffer.append(platform.getString(TPlatformDetail.id));
            if (i != size - 1) {
                buffer.append(",");
            }
        }
        exeNetworkReq(DeliveryAPI.unitNum(String.valueOf(buffer), courseId).build());
    }
}
