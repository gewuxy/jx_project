package jx.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.dialog.HintDialogMain;
import jx.csp.model.Profile;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkAPISetter.LoginAPI;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.TestActivity;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;

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

    private final int KIdRegister = 0;
    private final int KIdLogin = 1;
    private final int KReturnCode = 101;

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
                startActivity(TestActivity.class);
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
        exeNetworkReq(KIdRegister, LoginAPI.register(getEmail(), getUserPwd(), getNickname()).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdLogin) {
            return JsonParser.ev(r.getText(), Profile.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdLogin) {
            //登录
            stopRefresh();

            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                SpUser.inst().updateProfileRefreshTime();
                notify(NotifyType.login);
                //Fixme:跳到首页，目前还没有
                startActivity(TestActivity.class);
                finish();
            } else {
                onNetworkError(id, r.getError());
                finish();
            }
        } else if (id == KIdRegister) {
            //注册
            Result r = (Result) result;
            if (r.getCode() == KReturnCode) {
                stopRefresh();
                HintDialogMain d = new HintDialogMain(this);
                d.setHint(getString(R.string.phone_have_been_register));
                d.addBlueButton(R.string.cancel);
                d.addBlueButton(getString(R.string.immediately_login), v -> finish());
                d.show();
                return;
            }
            if (r.isSucceed()) {
                SpApp.inst().saveUserName(getNickname());
                //Fixme:原来注册还有个请求还有个packageUtil,什么鬼
                exeNetworkReq(KIdLogin, LoginAPI.login(LoginType.email_login).email(getEmail()).password(getUserPwd()).build());
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }

    @Override
    protected int getFooterResId() {
        return R.layout.layout_captcha_login_footer;
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(Util.getEtString(mEtEmail)) && TextUtil.isNotEmpty(Util.getEtString(mEtPwd))
                && TextUtil.isNotEmpty(Util.getEtString(mEtNickname)));
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
