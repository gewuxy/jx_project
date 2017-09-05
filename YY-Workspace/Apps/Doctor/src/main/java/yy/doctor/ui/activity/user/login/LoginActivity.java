package yy.doctor.ui.activity.user.login;

import android.support.annotation.NonNull;
import android.view.View;

import lib.network.model.NetworkError;
import lib.wx.WXLoginApi;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Constants;
import yy.doctor.Constants.WXType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.ForgetPwdDialog;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.dialog.HintDialogSec;
import yy.doctor.model.Profile;
import yy.doctor.sp.SpApp;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.ui.activity.user.register.RegisterActivity;

/**
 * 登录
 *
 * @author CaiXiang
 * @since 2017/4/19
 */
public class LoginActivity extends BaseLoginActivity {

    private ForgetPwdDialog mDialogForgetPwd;
    private String mRequest; // 判断桌面快捷方式进来

    private int mCount = 0;

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
        getEtName().setSelection(getUserName().length());

        //检查有没有定位权限
        checkPermission(0, Permission.location);
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
                mDialogForgetPwd = new ForgetPwdDialog(LoginActivity.this);
                mDialogForgetPwd.show();
            }
            break;
            case R.id.login_layout_wechat: {
                WXLoginApi.create(LoginActivity.this, Constants.KAppId);
                if (WXLoginApi.isWXAppInstalled()) {
                    WXLoginApi.sendReq(WXType.login);
                } else {
                    // 未安装微信
                    HintDialogSec d = new HintDialogSec(LoginActivity.this);
                    d.setMainHint(R.string.wx_accredit_error);
                    d.setSecHint(R.string.wx_check_normal);
                    d.addBlueButton(R.string.affirm);
                    d.show();
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
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            //保存用户名
            SpApp.inst().saveUserName(getUserName());
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();
            //判断跳转到哪里
            if (TextUtil.isEmpty(mRequest)) {
                startActivity(MainActivity.class);
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
            YSLog.d("lol", mCount + "次.....");
            if (mCount > 5 && mCount < 8) {
                HintDialogMain d = new HintDialogMain(this);
                d.setHint(getString(R.string.pwd_error));
                d.addGrayButton(R.string.cancel);
                d.addBlueButton("找回密码", v1 -> {
                    mDialogForgetPwd = new ForgetPwdDialog(LoginActivity.this);
                    mDialogForgetPwd.show();
                });
                d.show();
            }

            if (mCount == 8) {
                mCount = 1;
                YSLog.d("lol", mCount + "次.....");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WXLoginApi.detach();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }

}
