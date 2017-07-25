package yy.doctor.ui.activity.login;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpApp;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;
import yy.doctor.view.AutoCompleteEditText;

/**
 * @auther : GuoXuan
 * @since : 2017/7/18
 */
public abstract class BaseLoginActivity extends BaseActivity {

    private AutoCompleteEditText mEtName; // 用户名输入框
    private EditText mEtPwd; // 密码输入框
    private CheckBox mCbPwdVisible; // 密码是否可见

    private ImageView mIvNameClear; // 清除用户名
    private ImageView mIvPwdClear; // 清除密码

    private TextView mTvButton; // 按钮(登录 / 绑定并登录)

    public EditText getEtName() {
        return mEtName;
    }

    public String getUserName() {
        if (mEtName == null) {
            return "";
        }
        return Util.getEtString(mEtName);
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @CallSuper
    @Override
    public void findViews() {
        mEtName = findView(R.id.login_et_name);
        mEtPwd = findView(R.id.login_et_pwd);
        mCbPwdVisible = findView(R.id.login_cb_visible_pwd);
        mIvNameClear = findView(R.id.login_iv_cancel);
        mIvPwdClear = findView(R.id.login_iv_cancel_pwd);
        mTvButton = findView(R.id.login_tv_login);
    }

    @CallSuper
    @Override
    public void setViews() {
        mTvButton.setEnabled(false);
        mTvButton.setText(getBtnText());

        // 密码明文暗文(光标最后), 设置密码输入范围
        setPwdVisible(mEtPwd, mCbPwdVisible);
        // 监听mEtName,mEtPwd的内容改变
        buttonChanged(mEtName, mIvNameClear);
        buttonChanged(mEtPwd, mIvPwdClear);

        setOnClickListener(R.id.login_tv_login);
        setOnClickListener(R.id.login_cb_visible_pwd);
        setOnClickListener(R.id.login_iv_cancel);
        setOnClickListener(R.id.login_iv_cancel_pwd);
    }

    /**
     * 密码的明文或者暗文, 设置密码范围
     *
     * @param et
     * @param cb
     */
    private void setPwdVisible(EditText et, CheckBox cb) {
        UISetter.setPwdRange(et);
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            // 把光标设置到当前文本末尾
            et.setSelection(et.getText().length());
        });
    }

    private void buttonChanged(EditText et, ImageView iv) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((RegexUtil.isMobileCN(getUserName()) || RegexUtil.isEmail(getUserName()))
                        && !TextUtil.isEmpty(Util.getEtString(mEtPwd))) {
                    mTvButton.setEnabled(true);
                } else {
                    mTvButton.setEnabled(false);
                }
                // iv是否显示
                if (TextUtil.isEmpty(Util.getEtString(et))) {
                    hideView(iv);
                } else {
                    showView(iv);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @CallSuper
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_login: {
                refresh(AppConfig.RefreshWay.dialog);
                exeNetworkReq(NetFactory.login(getUserName(), Util.getEtString(mEtPwd), getOpenId()));
            }
            break;
            case R.id.login_iv_cancel: {
                mEtName.setText("");
                SpApp.inst().saveUserName("");
            }
            break;
            case R.id.login_iv_cancel_pwd: {
                mEtPwd.setText("");
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    protected abstract CharSequence getBtnText();

    @NonNull
    protected abstract String getOpenId();

}
