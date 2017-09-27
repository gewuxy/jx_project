package yaya.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import yaya.csp.Extra;
import yaya.csp.R;
import yaya.csp.dialog.HintDialogMain;
import yaya.csp.model.Profile;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;
import yaya.csp.model.login.login;
import yaya.csp.network.JsonParser;
import yaya.csp.network.NetworkAPISetter.LoginAPI;
import yaya.csp.sp.SpApp;
import yaya.csp.sp.SpUser;
import yaya.csp.ui.activity.TestActivity;
import yaya.csp.util.UISetter;
import yaya.csp.util.Util;

/**
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
    private String mRequest; // 判断桌面快捷方式进来
    private TextView mAgreeProtocol;
    private int mCount = 0;

    @Override
    public void initData() {
        super.initData();
        mRequest = getIntent().getStringExtra(Extra.KData);

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint(R.string.email_address)
                .layout(R.layout.form_edit_email))
                .drawable(R.drawable.login_ic_email)
                .paddingLeft(46);
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et_pwd))
                .related(RelatedId.pwd)
                .hint(R.string.input_pwd)
                .drawable(R.drawable.login_pwd_selector);
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void findViews() {
        super.findViews();
        mAgreeProtocol = findView(R.id.email_agree_protocol);
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

        mEtEmail.setText(SpApp.inst().getUserName());
        mEtEmail.setSelection(getUserName().length());

        mAgreeProtocol.setText(UISetter.setLoginProtocol(getString(R.string.agree_login)));
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
                startActivity(FindPwdActivity.class);
            }
            break;
        }
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(LoginAPI.login(7).email(getUserName()).password(getUserPwd()).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), login.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            //保存用户名，邮箱用户名是昵称？？？
            SpApp.inst().saveUserName(getUserName());
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();

            //判断跳转到哪里
            if (TextUtil.isEmpty(mRequest)) {
                //Fixme:跳转到首页，目前暂时没有
                startActivity(TestActivity.class);
            } else {
                setResult(RESULT_OK);
            }
            stopRefresh();
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
                HintDialogMain dialog = new HintDialogMain(this);
                dialog.setHint(getString(R.string.pwd_err));
                dialog.addGrayButton(R.string.cancel);
                dialog.addBlueButton(getString(R.string.find_pwd), v1 -> {
                    startActivity(FindPwdActivity.class);
                });
                dialog.show();
            }
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

    public String getUserName() {
        if (mEtEmail == null) {
            return "";
        }
        return Util.getEtString(mEtEmail);
    }

    public String getUserPwd() {
        if (mEtPwd == null) {
            return "";
        }
        return Util.getEtString(mEtPwd);
    }
}
