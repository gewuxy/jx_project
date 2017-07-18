package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;
import yy.doctor.ui.activity.ForgetPwdActivity;

/**
 * @auther HuoXuYu
 * @since 2017/7/18
 */

public class ForgetPwdTooltipDialog extends BaseDialog{

    private LinearLayout mForgetSms;
    private LinearLayout mForgetEmail;
    private ImageView mForgetClose;

    public ForgetPwdTooltipDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_forget_pwd_tooltip;
    }

    @Override
    public void findViews() {

        mForgetSms = findView(R.id.forget_pwd_ll_tooltip_sms);
        mForgetEmail = findView(R.id.forget_pwd_ll_tooltip_email);
        mForgetClose = findView(R.id.forget_pwd_iv_close);
    }

    @Override
    public void setViews() {

        setGravity(Gravity.CENTER_VERTICAL);
        setOnClickListener(R.id.forget_pwd_ll_tooltip_sms);
        setOnClickListener(R.id.forget_pwd_ll_tooltip_email);
        setOnClickListener(R.id.forget_pwd_iv_close);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_ll_tooltip_sms: {

            }
            break;
            case R.id.forget_pwd_ll_tooltip_email: {
                startActivity(ForgetPwdActivity.class);
            }
            break;
            case R.id.forget_pwd_iv_close: {
                dismiss();
            }
            break;
        }
    }
}
