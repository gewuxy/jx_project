package jx.csp.presenter;

import android.content.Context;

import java.io.File;

import jx.csp.contact.SettingsContract;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import lib.ys.YSLog;
import lib.ys.util.FileUtil;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public class SettingsPresenterImpl extends BasePresenterImpl<SettingsContract.V>
        implements SettingsContract.P {

    private final String KM = "M";

    public SettingsPresenterImpl(SettingsContract.V v) {
        super(v);
    }

    @Override
    public String getFolderSize(String... path) {
        float size = 0;
        try {
            for (String s : path) {
                size += FileUtil.getFolderSize(new File(s));
            }
        } catch (Exception e) {
            YSLog.e(TAG, "getFolderSize", e);
        }
        size /= (2 << 19);
        if (size >= 0.1f) {
            return String.format("%.1f".concat(KM), size);
        } else {
            return 0 + KM;
        }
    }

    @Override
    public void startLogoutService(Context context) {
        CommonServRouter.create()
                .type(ReqType.logout)
                .route(context);

    }
}
