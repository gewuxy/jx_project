package jx.csp.ui.activity.login;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.constant.FormType;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.Profile;
import jx.csp.model.form.Form;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.main.MainActivity;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.notify.Notifier.NotifyType;

//import jx.csp.model.login.login;
;

/**
 * 邮箱登录
 *
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

    private View mLayout;
    private View mLayoutRegister;

    private int mCount = 0;

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.email_address)
                .textWatcher(this)
                .layout(R.layout.form_edit_email));
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et_pwd))
                .related(RelatedId.pwd)
                .hint(R.string.input_pwd)
                .textWatcher(this)
                .drawable(R.drawable.login_selector_visible);
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void findViews() {
        super.findViews();
        mLayout = findView(R.id.layout_email_login);
        mLayoutRegister = findView(R.id.layout_register);
    }

    @Override
    public void setViews() {
        super.setViews();

        //清空用户信息
        Profile.inst().clear();

        setOnClickListener(R.id.login_tv_register);
        setOnClickListener(R.id.login_tv_forget_pwd);
        setOnClickListener(R.id.protocol);

        getRelatedItem(RelatedId.email).getHolder().getEt().setText(SpApp.inst().getUserEmail());

        showView(mLayout);
        showView(mLayoutRegister);
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
                CommonWebViewActivityRouter.create(UrlUtil.getUrlDisclaimer()).name(getString(R.string.service_agreement))
                        .route(this);
            }
            break;
        }
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.login(BindId.email).email(getEmail()).password(getUserPwd()).build());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            // FIXME: 怎么使用的getEmail??
            SpApp.inst().saveUserEmail(getEmail());
            Profile.inst().update((Profile) r.getData());
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
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(getEmail()) && TextUtil.isNotEmpty(getUserPwd()));
    }

    public String getEmail() {
        return getRelatedItem(RelatedId.email).getVal();
    }

    public String getUserPwd() {
        return getRelatedItem(RelatedId.pwd).getVal();
    }

}
