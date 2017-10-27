package jx.csp.presenter;

import jx.csp.contact.MyMessageContract;
import jx.csp.contact.MyMessageContract.V;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import lib.network.model.NetworkResp;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class MyMessagePresenterImpl extends PresenterExImpl<MyMessageContract.V> implements MyMessageContract.P{

    private MyMessageContract.V mView;

    private final int KNickNameCode = 0;
    private final int KInfoCode = 1;

    public MyMessagePresenterImpl(V v) {
        super(v);
        mView = v;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            mView.saveModifySuccess();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void getModifyReq(int id, String text) {
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
