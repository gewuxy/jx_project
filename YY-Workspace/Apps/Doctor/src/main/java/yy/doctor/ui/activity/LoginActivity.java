package yy.doctor.ui.activity;

import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.ui.activity.register.RegisterActivity;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpApp;
import yy.doctor.view.AutoCompleteEditText;

/**
 * 登录
 *
 * @author CaiXiang
 * @since 2017/4/19
 */
public class LoginActivity extends BaseActivity {

    private TextView mTvVersion;
    private AutoCompleteEditText mEtName;
    private EditText mEtPwd;
    private String mRequest;

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
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {

        mTvVersion = findView(R.id.login_tv_version);
        mEtName = findView(R.id.login_et_name);
        mEtPwd = findView(R.id.login_et_pwd);
    }

    @Override
    public void setViews() {

        // 设置密码输入范围
        mEtPwd.setKeyListener(new NumberKeyListener() {

            @Override
            protected char[] getAcceptedChars() {
                String chars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM@#$%^&";
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });

        setOnClickListener(R.id.login_tv);
        setOnClickListener(R.id.login_tv_register);
        setOnClickListener(R.id.login_tv_forget_pwd);

        mTvVersion.setText(DeviceUtil.getAppVersionName());
        mEtName.setText(SpApp.inst().getUserName());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.login_tv: {

                String strName = mEtName.getText().toString().trim();
                String strPwd = mEtPwd.getText().toString().trim();
                if (TextUtil.isEmpty(strName)) {
                    showToast(R.string.input_name);
                    return;
                }
                if (TextUtil.isEmpty(strPwd)) {
                    showToast(R.string.input_pwd);
                    return;
                }
                refresh(RefreshWay.dialog);
                exeNetworkReq(NetFactory.login(strName, strPwd));
            }
            break;
            case R.id.login_tv_register: {
                startActivity(RegisterActivity.class);
            }
            break;
            case R.id.login_tv_forget_pwd: {
                startActivity(ForgetPwdActivity.class);
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();

        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            //保存用户名
            SpApp.inst().saveUserName(mEtName.getText().toString().trim());
            Profile.inst().clear();
            Profile.inst().update(r.getData());
            //判断跳转到哪里
            if (mRequest == null) {
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
    public void onNotify(@NotifyType int type, Object data) {
        super.onNotify(type, data);

        if (type == NotifyType.login) {
            LoginActivity.this.finish();
        }
    }

}