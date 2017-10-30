package jx.csp.presenter;

import android.content.Context;

import jx.csp.contact.SettingsContract;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public class SettingsPresenterImpl extends BasePresenterImpl<SettingsContract.V> implements SettingsContract.P{

    public SettingsPresenterImpl(SettingsContract.V v) {
        super(v);
    }

    @Override
    public void startLogoutService(Context context) {
        CommonServRouter.create()
                .type(ReqType.logout)
                .route(context);

    }
}
