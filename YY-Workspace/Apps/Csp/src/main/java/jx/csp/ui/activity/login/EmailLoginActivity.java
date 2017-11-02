package jx.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.Profile;
import jx.csp.model.form.Form;
import jx.csp.model.def.FormType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.util.Util;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;

//import jx.csp.model.login.login;
;

/**
 * 邮箱登录
 * @auther WangLan
 * @since 2017/9/22
 */

public class EmailLoginActivity extends BaseLoginActivity {

    @IntDef({
            RelatedId.email,
            RelatedId.pwd,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
        int pwd = 1;
    }

    private EditText mEtEmail;
    private EditText mEtPwd;
    private int mCount = 0;

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.email_address)
                .layout(R.layout.form_edit_email))
                /*.paddingLeft(46)*/;
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et_pwd))
                .related(RelatedId.pwd)
                .hint(R.string.input_pwd)
                .drawable(R.drawable.login_pwd_selector);
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void setViews() {
        super.setViews();

        //清空用户信息
        Profile.inst().clear();
        mEtEmail = getRelatedItem(RelatedId.email).getHolder().getEt();
        mEtEmail.addTextChangedListener(this);
        mEtPwd = getRelatedItem(RelatedId.pwd).getHolder().getEt();
        mEtPwd.addTextChangedListener(this);

        setOnClickListener(R.id.login_tv_register);
        setOnClickListener(R.id.login_tv_forget_pwd);
        setOnClickListener(R.id.protocol);

        mEtEmail.setText(SpApp.inst().getUserEmail());
        mEtEmail.setSelection(getEmail().length());
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.email_login);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.confirm_login);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.login_tv_register: {
                startActivity(RegisterActivity.class);
            }
            break;
            case R.id.login_tv_forget_pwd: {
                startActivity(ForgetPwdActivity.class);
            }
            break;
            case R.id.protocol: {
                //Fixme:跳转到h5页面，现在还没有文案
                showToast("没有文案，先酱紫，哈哈");
            }
            break;
        }
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        //Fixme:原来登录请求还有个packageUtil,什么鬼
        exeNetworkReq(UserAPI.login(LoginType.email_login).email(getEmail()).password(getUserPwd()).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            SpApp.inst().saveUserEmail(getEmail());
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();

            notify(NotifyType.login);
            startActivity(MainActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);
        if (error.getCode() == ErrorCode.KPwdErr) {
            mCount++;
            if (mCount > 5 && mCount < 8) {
                CommonDialog2 dialog = new CommonDialog2(this);
                dialog.setHint(getString(R.string.pwd_err));
                dialog.addGrayButton(R.string.cancel);
                dialog.addBlueButton(getString(R.string.find_pwd), v1 -> {
                    startActivity(ForgetPwdActivity.class);
                });
                dialog.show();
            }
            if (mCount == 8) {
                mCount = 1;
            }
        } else {
            showToast(error.getMessage());
        }
    }

    @Override
    protected int getFooterResId() {
        return R.layout.layout_email_login_footer;
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(Util.getEtString(mEtEmail)) && TextUtil.isNotEmpty(Util.getEtString(mEtPwd)));
    }

    public String getEmail() {
        return Util.getEtString(mEtEmail);
    }

    public String getUserPwd() {
        return Util.getEtString(mEtPwd);
    }

}
