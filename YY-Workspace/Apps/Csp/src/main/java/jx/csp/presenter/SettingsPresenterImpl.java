package jx.csp.presenter;

import android.content.Context;

import java.io.File;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.SettingsContract;
import jx.csp.dialog.BottomDialog;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.login.AuthLoginOverseaActivity;
import jx.csp.ui.activity.me.SettingsActivity;
import jx.csp.ui.activity.me.bind.BindEmailTipsActivity;
import jx.csp.ui.activity.me.bind.ChangePwdActivity;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.util.FileUtil;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public class SettingsPresenterImpl extends BasePresenterImpl<SettingsContract.V>
        implements SettingsContract.P {

    private final String KM = "M";
    private final int KColorNormal = R.color.text_666;
    private final int KColorCancel = R.color.text_167afe;

    public SettingsPresenterImpl(SettingsContract.V v) {
        super(v);
    }

    @Override
    public void changePassWord() {
        if (TextUtil.isEmpty(Profile.inst().getString(TProfile.email))) {
            LaunchUtil.startActivity(SettingsActivity.class, BindEmailTipsActivity.class);
        } else {
            //已绑定邮箱,直接跳转到修改页面
            LaunchUtil.startActivity(SettingsActivity.class, ChangePwdActivity.class);
        }
    }

    @Override
    public void clearCache(Context context, int related, String resId, String cancel, String... folderPath) {
        BottomDialog d = new BottomDialog(context, position -> {

            if (position == 0) {
                Util.runOnSubThread(() -> {
                    boolean result = true;
                    for (int i = 0; i < folderPath.length; ++i) {
                        if (!FileUtil.delFolder(folderPath[i])) {
                            result = false;
                            break;
                        }
                    }

                    if (result) {
                        Util.runOnUIThread(() -> {
                            getView().refreshItem(related);
                            App.showToast(R.string.setting_clear_succeed);
                        });
                    } else {
                        App.showToast(R.string.setting_clear_error);
                    }
                });
            }
        });
        d.addItem(resId, ResLoader.getColor(KColorNormal));
        d.addItem(cancel, ResLoader.getColor(KColorCancel));
        d.show();
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
        CommonServRouter.create(ReqType.logout).route(context);
    }

    @Override
    public void logout(Context context) {
        CommonDialog2 d = new CommonDialog2(context);
        d.setHint(R.string.setting_exit_current_account);
        d.addBlackButton(R.string.setting_exit, v -> {
            startLogoutService(context);

            if (Util.checkAppCn()) {
                LaunchUtil.startActivity(context, AuthLoginActivity.class);
            } else {
                LaunchUtil.startActivity(context, AuthLoginOverseaActivity.class);
            }
            getView().closePage();
        });
        d.addBlueButton(R.string.cancel);
        d.show();
    }
}
