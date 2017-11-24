package jx.csp.ui.activity.login;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.Editable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.model.form.Form;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;

/**
 * 忘记密码
 *
 * @auther WangLan
 * @since 2017/9/25
 */

public class ForgetPwdActivity extends BaseLoginActivity {

    @IntDef({
            RelatedId.email,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
    }

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.input_register_email)
                .layout(R.layout.form_edit_email))
                .textWatcher(this)
                .drawable(R.drawable.login_ic_email);
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.find_pwd);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.send_email);
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(CommonAPI.findPwd(getEmail()).build());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            SkipActivityRouter.create(getString(R.string.find_pwd),getString(R.string.reset_pwd_to_email)).route(this);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(getEmail()));
    }

    public String getEmail() {
        return getRelatedString(RelatedId.email);
    }
}
