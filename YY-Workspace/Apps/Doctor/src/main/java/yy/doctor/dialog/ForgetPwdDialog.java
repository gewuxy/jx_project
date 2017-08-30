package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;
import yy.doctor.ui.activity.user.ForgetPwdEmailActivity;
import yy.doctor.ui.activity.user.ForgetPwdPhoneActivity;

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
    public void initData() {

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

        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_layout_tooltip_sms: {
                startActivity(ForgetPwdPhoneActivity.class);
                dismiss();
            }
            break;
            case R.id.forget_pwd_layout_tooltip_email: {
                startActivity(ForgetPwdEmailActivity.class);
                dismiss();
            }
            break;
            case R.id.forget_pwd_iv_close: {
                dismiss();
            }
            break;
        }
    }
}
