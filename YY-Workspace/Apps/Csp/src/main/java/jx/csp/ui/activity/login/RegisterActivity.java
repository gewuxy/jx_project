package jx.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.dialog.HintDialogMain;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;

/**
 * @auther WangLan
 * @since 2017/9/26
 */

public class RegisterActivity extends BaseLoginActivity {

    @IntDef({
            RelatedId.email,
            RelatedId.pwd,
            RelatedId.nickname,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
        int pwd = 1;
        int nickname = 2;
    }

    private final int KReturnCode = 101; // 邮箱已经注册后台返回值

    private EditText mEtEmail;
    private EditText mEtPwd;
    private EditText mEtNickname;

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.email_address)
                .layout(R.layout.form_edit_email))
                .drawable(R.drawable.login_ic_email);
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et_pwd))
                .related(RelatedId.pwd)
                .hint(R.string.input_pwd)
                .drawable(R.drawable.login_pwd_selector);
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et)
                .related(RelatedId.nickname)
                .hint(R.string.input_nickname)
                .drawable(R.drawable.login_ic_nickname));
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void setViews() {
        super.setViews();
        mEtEmail = getRelatedItem(RelatedId.email).getHolder().getEt();
        mEtEmail.addTextChangedListener(this);

        mEtPwd = getRelatedItem(RelatedId.pwd).getHolder().getEt();
        mEtPwd.addTextChangedListener(this);

        mEtNickname = getRelatedItem(RelatedId.nickname).getHolder().getEt();
        mEtNickname.addTextChangedListener(this);

        setOnClickListener(R.id.protocol);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.protocol: {
                //Fixme:跳转到h5页面，现在还没有文案
                showToast("没有文案，先酱紫，哈哈");
            }
            break;
        }
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.register);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.immediately_register);
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
        exeNetworkReq(UserAPI.register(getEmail(), getUserPwd(), getNickname()).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        //注册
        Result r = (Result) result;
        if (r.getCode() == KReturnCode) {
            HintDialogMain d = new HintDialogMain(this);
            d.setHint(getString(R.string.email_have_been_register));
            d.addBlueButton(R.string.cancel);
            d.addBlueButton(getString(R.string.immediately_login), v -> finish());
            d.show();
            return;
        }
        if (r.isSucceed()) {
            Profile.inst().put(TProfile.nickName, getNickname());
            Profile.inst().put(TProfile.email, getNickname());
            Profile.inst().saveToSp();
            startActivity(RegisterSkipActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    protected int getFooterResId() {
        return R.layout.layout_captcha_login_footer;
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(getEmail()) && TextUtil.isNotEmpty(getUserPwd()) && TextUtil.isNotEmpty(getNickname()));
    }

    public String getEmail() {
        return Util.getEtString(mEtEmail);
    }

    public String getUserPwd() {
        return Util.getEtString(mEtPwd);
    }

    public String getNickname() {
        return Util.getEtString(mEtNickname);
    }
}
