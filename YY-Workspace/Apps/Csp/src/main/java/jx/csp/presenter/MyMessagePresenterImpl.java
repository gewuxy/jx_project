package jx.csp.presenter;

import jx.csp.contact.MyMessageContract;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import lib.network.model.interfaces.IResult;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class MyMessagePresenterImpl extends BasePresenterImpl<MyMessageContract.V> implements MyMessageContract.P {

    private final int KNickNameCode = 0;
    private final int KInfoCode = 1;

    public MyMessagePresenterImpl(MyMessageContract.V v) {
        super(v);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (r.isSucceed()) {
            getView().saveRevisedData();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void savePersonMessage(int id, String text) {
        switch (id) {
            case KNickNameCode: {
                exeNetworkReq(id, UserAPI.modify().nickName(text).build());
            }
            break;
            case KInfoCode: {
                exeNetworkReq(id, UserAPI.modify().info(text).build());
            }
            break;
        }
    }
}
