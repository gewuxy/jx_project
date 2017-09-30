package jx.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.network.NetworkAPISetter.LoginAPI;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.yy.network.Result;

/**
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

    private EditText mEtEmail;

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.input_register_email)
                .layout(R.layout.form_edit_email))
                .drawable(R.drawable.login_ic_email);
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void setViews() {
        super.setViews();
        mEtEmail = getRelatedItem(RelatedId.email).getHolder().getEt();
        mEtEmail.addTextChangedListener(this);
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
        exeNetworkReq(LoginAPI.findPwd(getEmail()).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return super.onNetworkResponse(id, r);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            startActivity(ForgetPwdSkipActivity.class);
            finish();
        }else {
            onNetworkError(id,r.getError());
        }
    }

    @Override
    protected int getFooterResId() {
        return 0;
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(Util.getEtString(mEtEmail)));
    }

    public String getEmail() {
        if (mEtEmail == null) {
            return "";
        }
        return Util.getEtString(mEtEmail);
    }
}
