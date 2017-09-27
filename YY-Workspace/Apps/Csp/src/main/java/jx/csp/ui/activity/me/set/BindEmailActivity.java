package jx.csp.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.yy.network.Result;
import jx.csp.R;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.network.NetworkAPISetter.UserAPI;
import jx.csp.util.Util;

/**
 * 认证邮箱
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class BindEmailActivity extends BaseSetActivity{

    private EditText mEt;

    @IntDef({
            RelatedId.email,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.setting_input_email_address)
                .layout(R.layout.form_edit_bind_email));
    }

    @Override
    public void setViews() {
        super.setViews();

        mEt = getRelatedItem(RelatedId.email).getHolder().getEt();

        mEt.addTextChangedListener(this);
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
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.bindEmail(Util.getEtString(mEt), "d48f972107584add99e48adc510fdb35").build());
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(Util.getEtString(mEt)));
    }
}
