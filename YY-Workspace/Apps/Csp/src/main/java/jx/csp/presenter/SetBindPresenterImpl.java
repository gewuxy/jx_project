package jx.csp.presenter;

import android.widget.EditText;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.SetBindContract;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.BaseSetActivity.RelatedId;
import jx.csp.ui.activity.me.bind.BindEmailActivity;
import jx.csp.ui.activity.me.bind.ReceiveEmailTipsActivity;
import jx.csp.util.Util;
import lib.network.model.interfaces.IResult;
import lib.ys.util.LaunchUtil;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/31
 */

public class SetBindPresenterImpl extends BasePresenterImpl<SetBindContract.V> implements SetBindContract.P {

    private final int KBindEmailCode = 0;
    private final int KChangePwdCode = 4;

    public SetBindPresenterImpl(SetBindContract.V v) {
        super(v);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (!r.isSucceed()) {
            onNetworkError(id, r.getError());
            return;
        }
        switch (id) {
            case KBindEmailCode: {
                LaunchUtil.startActivity(BindEmailActivity.class, ReceiveEmailTipsActivity.class);
                getView().closePage();
                App.showToast(R.string.setting_bind_email_succeed);
            }
            break;
            case KChangePwdCode: {
                getView().closePage();
                App.showToast(R.string.setting_change_pwd_succeed);
            }
            break;
        }
    }

    @Override
    public void checkPwd(EditText et) {
        if (!Util.checkPwd(Util.getEtString(et))) {
            return;
        }
    }

    @Override
    public void confirmBindAccount(@RelatedId int id, String userName, String num) {
        exeNetworkReq(id, UserAPI.bindEmail(userName, num).build());
    }

    @Override
    public void modifyPwd(@RelatedId int id, String oldPwd, String newPwd) {
        exeNetworkReq(id, UserAPI.changePwd(oldPwd, newPwd).build());
    }

}
