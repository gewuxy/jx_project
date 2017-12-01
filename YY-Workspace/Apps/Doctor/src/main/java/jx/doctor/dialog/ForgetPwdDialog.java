package jx.doctor.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import lib.yy.dialog.BaseDialog;
import jx.doctor.R;
import jx.doctor.ui.activity.user.ForgetPwdEmailActivity;
import jx.doctor.ui.activity.user.ForgetPwdPhoneActivity;

/**
 * 登陆界面-找回密码弹出的Dialog
 *
 * @auther HuoXuYu
 * @since 2017/7/18
 */
public class ForgetPwdDialog extends BaseDialog {

    public ForgetPwdDialog(Context context) {
        super(context);
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_forget_pwd;
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setGravity(Gravity.CENTER_VERTICAL);
        setOnClickListener(R.id.forget_pwd_layout_tooltip_sms);
        setOnClickListener(R.id.forget_pwd_layout_tooltip_email);
        setOnClickListener(R.id.forget_pwd_iv_close);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_pwd_layout_tooltip_sms: {
                startActivity(ForgetPwdPhoneActivity.class);
            }
            break;
            case R.id.forget_pwd_layout_tooltip_email: {
                startActivity(ForgetPwdEmailActivity.class);
            }
            break;
        }
        dismiss();
    }
}
