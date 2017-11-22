package jx.csp.presenter;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.MyMessageContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
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

    private String mText;

    public MyMessagePresenterImpl(MyMessageContract.V v) {
        super(v);
    }

    @Override
    public void savePersonMessage(int id, String text) {
        mText = text;
        switch (id) {
            case KNickNameCode: {
                exeNetworkReq(id, UserAPI.modify().nickName(mText).build());
            }
            break;
            case KInfoCode: {
                exeNetworkReq(id, UserAPI.modify().info(mText).build());
            }
            break;
        }
    }

    @Override
    public void saveLocal(TProfile profile) {
        Profile.inst().put(profile, mText);
        Profile.inst().saveToSp();

        getView().setData(mText);

        App.showToast(R.string.my_message_save_success);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (r.isSucceed()) {
            if (id == KNickNameCode) {
                saveLocal(TProfile.nickName);
            }else {
                saveLocal(TProfile.info);
            }
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
