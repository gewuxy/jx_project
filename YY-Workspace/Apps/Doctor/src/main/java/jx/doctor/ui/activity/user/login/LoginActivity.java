package jx.doctor.ui.activity.user.login;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import jx.doctor.Constants;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.dialog.ForgetPwdDialog;
import jx.doctor.dialog.HintDialogMain;
import jx.doctor.dialog.HintDialogSec;
import jx.doctor.model.Profile;
import jx.doctor.sp.SpApp;
import jx.doctor.sp.SpUser;
import jx.doctor.ui.activity.MainActivity;
import jx.doctor.ui.activity.user.register.RegisterActivity;
import lib.jx.network.BaseJsonParser.ErrorCode;
import lib.jx.notify.Notifier.NotifyType;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.util.PackageUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.res.ResLoader;
import lib.ys.util.res.ResUtil.ResDefType;

//import lib.wx.WXLoginApi;

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

    private ImageView mIvLogo;

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
    public void findViews() {
        super.findViews();

        mIvLogo = findView(R.id.login_ic);
    }

    @Override
    public void setViews() {
        super.setViews();

        // 清空用户信息
        Profile.inst().clear();
        mIvLogo.setImageResource(ResLoader.getIdentifier(PackageUtil.getMetaValue("LOGIN_ICON"), ResDefType.drawable));
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
                WXLoginApi.create(LoginActivity.this, Constants.KWXAppId);
                if (WXLoginApi.isWXAppInstalled()) {
                    WXLoginApi.sendReq(Constants.WXType.login);
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
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            //保存用户名
            SpApp.inst().saveUserName(getUserName());
            Profile.inst().update((Profile) r.getData());
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
