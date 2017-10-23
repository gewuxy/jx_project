package jx.csp.ui.activity.me.bind;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.util.Util;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;

/**
 * 认证邮箱
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class BindEmailActivity extends BaseSetActivity{

    private EditText mEtEmail;
    private EditText mEtPwd;

    @IntDef({
            RelatedId.email,
            RelatedId.pwd,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
        int pwd = 1;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.setting_input_email_address)
                .layout(R.layout.form_edit_bind_email));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd)
                .hint(R.string.input_pwd)
                .drawable(R.drawable.login_pwd_selector));
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtEmail = getRelatedItem(RelatedId.email).getHolder().getEt();
        mEtPwd= getRelatedItem(RelatedId.pwd).getHolder().getEt();

        mEtEmail.addTextChangedListener(this);
        mEtPwd.addTextChangedListener(this);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast(R.string.setting_bind_email_succeed);
            startActivity(BindChangeEmailActivity.class);
            finish();
        }else {
            showToast(r.getMessage());
        }
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.setting_bind_email);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.setting_certification_email);
    }

    @Override
    protected void toSet() {
        if (!getUserPwd().matches(Util.symbol())) {
            showToast(R.string.input_special_symbol);
            return;
        }
        if (getUserPwd().length() < 6) {
            showToast(R.string.input_right_pwd_num);
            return;
        }
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.bindEmail(getEmail(), getUserPwd()).build());
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(getEmail()) && TextUtil.isNotEmpty(getUserPwd()));
    }

    public String getEmail() {
        return Util.getEtString(mEtEmail);
    }

    public String getUserPwd() {
        return Util.getEtString(mEtPwd);
    }
}
