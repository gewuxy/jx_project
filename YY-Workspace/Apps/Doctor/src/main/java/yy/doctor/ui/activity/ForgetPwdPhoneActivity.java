package yy.doctor.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

/**
 * 手机短信找回密码
 *
 * @auther HuoXuYu
 * @since 2017/7/19
 */

public class ForgetPwdPhoneActivity extends BaseActivity{

    private EditText mEtPhone;
    private EditText mEtCaptcha;
    private EditText mEtNewPwd;
    private CheckBox mCbVisiblePwd;
    private Button mBtnCaptcha;
    private ImageView mIvCancelPhone;
    private ImageView mIvCancelPwd;
    private ImageView mIvCancelCaptcha;
    private TextView mTvLogin;
    private String mRequest;

    public String getUserName(){
        if (mEtPhone == null) {
            return "";
        }
        return mEtPhone.getText().toString().trim();
    }

    @Override
    public void initData() {
        mRequest = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_forget_pwd_phone;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.forget_pwd, this);
    }

    @Override
    public void findViews() {
        mEtPhone = findView(R.id.forget_pwd_et_phone);
        mEtCaptcha = findView(R.id.forget_pwd_et_captcha);
        mEtNewPwd = findView(R.id.forget_et_new_pwd);
        mIvCancelPhone = findView(R.id.forget_iv_cancel_phone);
        mIvCancelPwd = findView(R.id.forget_iv_cancel_pwd);
        mIvCancelCaptcha = findView(R.id.forget_iv_cancel_captcha);
        mCbVisiblePwd = findView(R.id.forget_cb_visible_pwd);
        mBtnCaptcha = findView(R.id.btn_captcha);
        mTvLogin = findView(R.id.forget_tv_login);
    }

    @Override
    public void setViews() {
        mTvLogin.setEnabled(false);
        mBtnCaptcha.setEnabled(false);

        setPwdVisible(mEtNewPwd, mCbVisiblePwd);
        buttonChanged(mEtPhone, mIvCancelPhone);
        buttonChanged(mEtNewPwd, mIvCancelPwd);
        buttonChanged(mEtCaptcha, mIvCancelCaptcha);

        setOnClickListener(R.id.forget_tv_login);
        setOnClickListener(R.id.forget_iv_cancel_phone);
        setOnClickListener(R.id.forget_iv_cancel_pwd);
        setOnClickListener(R.id.forget_iv_cancel_captcha);

    }

    /**
     * 密码明文暗文, 设置密码输入范围
     *
     * @param et
     * @param cb
     */

    private void setPwdVisible(EditText et, CheckBox cb) {
        UISetter.setPwdRange(et);
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else {
                et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            et.setSelection(et.getText().length());
        });
    }

    /**
     * 监听mEtPhone, mEtNewPwd, mEtValidationCode的内容改变
     *
     * @param et
     * @param iv
     */

    private void buttonChanged(EditText et, ImageView iv) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegexUtil.isMobileCN(getUserName())) {
                    mBtnCaptcha.setEnabled(true);
                }else {
                    mBtnCaptcha.setEnabled(false);
                }

                if (RegexUtil.isMobileCN(getUserName())
                        && !TextUtil.isEmpty(mEtCaptcha.getText().toString())
                        && !TextUtil.isEmpty(mEtNewPwd.getText().toString())) {
                    mTvLogin.setEnabled(true);
                }else {
                    mTvLogin.setEnabled(false);
                }

                if (TextUtil.isEmpty(et.getText().toString())) {
                    hideView(iv);
                }else {
                    showView(iv);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.forget_tv_login: {
                startActivity(MainActivity.class);
            }
            break;
            case R.id.forget_iv_cancel_phone: {
                mEtPhone.setText("");
            }
            break;
            case R.id.forget_iv_cancel_captcha: {
                mEtCaptcha.setText("");
            }
            break;
            case R.id.forget_iv_cancel_pwd: {
                mEtNewPwd.setText("");
            }
            break;
        }
    }

//    @Override
//    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
//        return JsonParser.ev(r.getText(), Profile.class);
//    }
//
//    @Override
//    public void onNetworkSuccess(int id, Object result) {
//        Result<Profile> r = (Result<Profile>) result;
//        if (r.isSucceed()) {
//            SpApp.inst().saveUserName(getUserName());
//            Profile.inst().update(r.getData());
//            //判断跳转到哪里
//            if (TextUtil.isEmpty(mRequest)) {
//                startActivity(MainActivity.class);
//            } else {
//                setResult(RESULT_OK);
//            }
//            finish();
//        } else {
//            showToast(r.getError());
//        }
//    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }
}
