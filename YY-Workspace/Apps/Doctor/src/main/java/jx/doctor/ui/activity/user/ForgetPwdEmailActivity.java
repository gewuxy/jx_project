package jx.doctor.ui.activity.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.dialog.HintDialogMain;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.UserAPI;
import jx.doctor.ui.activity.user.login.LoginActivity;
import jx.doctor.util.Util;
import jx.doctor.view.AutoCompleteEditText;

/**
 * 邮件找回密码
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class ForgetPwdEmailActivity extends BaseActivity {

    private AutoCompleteEditText mEt;
    private ImageView mIvCancel;
    private TextView mTvSendEmail;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_forget_pwd_email;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.forget_pwd, this);
    }

    @Override
    public void findViews() {

        mEt = findView(R.id.forget_pwd_et_email);
        mIvCancel = findView(R.id.forget_email_iv_cancel);
        mTvSendEmail = findView(R.id.forget_pwd_tv_send_email);
    }

    @Override
    public void setViews() {
        mTvSendEmail.setEnabled(false);

        buttonChanged(mEt, mIvCancel);

        setOnClickListener(R.id.forget_pwd_tv_send_email);
        setOnClickListener(R.id.forget_email_iv_cancel);
    }

    private void buttonChanged(AutoCompleteEditText et, ImageView iv) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegexUtil.isEmail(et.getText().toString().trim())) {
                    mTvSendEmail.setEnabled(true);
                } else {
                    mTvSendEmail.setEnabled(false);
                }

                if (TextUtil.isEmpty(et.getText().toString())) {
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

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_tv_send_email: {
                if (TextUtil.isEmpty(mEt.getText().toString().trim())) {
                    showToast(R.string.input_email);
                    return;
                }
                //检查邮箱
                if (!RegexUtil.isEmail(mEt.getText().toString().trim())) {
                    showToast(R.string.input_right_email);
                    return;
                }
                exeNetworkReq(UserAPI.email(mEt.getText().toString().trim()).build());
            }
            break;
            case R.id.forget_email_iv_cancel: {
                mEt.setText("");
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            HintDialogMain d = new HintDialogMain(ForgetPwdEmailActivity.this);
            d.setHint(getString(R.string.forget_pwd_success));
            d.addBlueButton(getString(R.string.know), v -> {
                startActivity(LoginActivity.class);
                finish();
            });
            d.show();
        } else {
            showToast(r.getMessage());
        }
    }

}
