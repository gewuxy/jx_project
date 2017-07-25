package yy.doctor.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import lib.wx.WXLoginApi;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Constants;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.ForgetPwdTooltipDialog;
import yy.doctor.dialog.HintDialogSec;
import yy.doctor.model.Profile;
import yy.doctor.sp.SpApp;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.ui.activity.RegisterActivity;

/**
 * 登录
 *
 * @author CaiXiang
 * @since 2017/4/19
 */
public class LoginActivity extends BaseLoginActivity {

    private ForgetPwdTooltipDialog mDialogForgetPwd;
    private String mRequest; // 判断桌面快捷方式进来
    private HintDialogSec mDialogWX; // 提示未安装微信

    @Override
    public void initData() {
        mRequest = getIntent().getStringExtra(Extra.KData);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void setViews() {
        super.setViews();

        // 清空用户信息
        Profile.inst().clear();

        setOnClickListener(R.id.login_tv_register);
        setOnClickListener(R.id.login_tv_forget_pwd);
        setOnClickListener(R.id.login_layout_wechat);

        getEtName().setText(SpApp.inst().getUserName());

        WXLoginApi.create(this, Constants.KAppId);
    }

    @Override
    protected CharSequence getBtnText() {
        return "登录";
    }

    @Override
    protected String getOpenId() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_register: {
                startActivity(RegisterActivity.class);
            }
            break;
            case R.id.login_tv_forget_pwd: {
                mDialogForgetPwd = new ForgetPwdTooltipDialog(LoginActivity.this);
                mDialogForgetPwd.show();
            }
            break;
            case R.id.login_layout_wechat: {
                if (WXLoginApi.isWXAppInstalled()) {
                    WXLoginApi.sendReq();
                } else {
                    // 未安装微信
                    mDialogWX = new HintDialogSec(LoginActivity.this);
                    mDialogWX.setMainHint(R.string.wx_accredit_error);
                    mDialogWX.setSecHint(R.string.wx_check_normal);
                    mDialogWX.addButton(R.string.affirm, v1 -> mDialogWX.dismiss());
                    mDialogWX.show();
                }
            }
            break;
            default: {
                super.onClick(v);
            }
            break;
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            //保存用户名
            SpApp.inst().saveUserName(getUserName());
            Profile.inst().update(r.getData());
            //判断跳转到哪里
            if (TextUtil.isEmpty(mRequest)) {
                startActivity(MainActivity.class);
            } else {
                setResult(RESULT_OK);
            }
            finish();
        } else {
            showToast(r.getError());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialogWX != null) {
            mDialogWX.dismiss();
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }

}
