package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import lib.network.model.NetworkResponse;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Response;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.activity.me.ForgetPwdActivity;
import yy.doctor.activity.register.RegisterActivity;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 登录
 *
 * @author CaiXiang
 * @since 2017/4/19
 */
public class LoginActivity extends BaseActivity {

    private EditText mEtName;
    private EditText mEtPwd;

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

    @Override
    public void findViews() {

        mEtName = findView(R.id.login_et_name);
        mEtPwd = findView(R.id.login_et_pwd);

    }

    @Override
    public void setViews() {

        setOnClickListener(R.id.login_tv);
        setOnClickListener(R.id.login_tv_register);
        setOnClickListener(R.id.login_tv_forget_pwd);

        if (BuildConfig.TEST) {
            mEtName.setText("18194529@qq.com");
            mEtPwd.setText("123456");
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.login_tv: {

                String strName = mEtName.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if (strName.isEmpty()) {
                    showToast("请输入用户名");
                    return;
                }
                if (strPwd.isEmpty()) {
                    showToast("请输入密码");
                    return;
                }

                refresh(RefreshWay.dialog);
                exeNetworkRequest(0, NetFactory.login(strName, strPwd));
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
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        LogMgr.d(TAG, nr.getText());


        return JsonParser.ev(nr.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();

        Response<Profile> r = (Response<Profile>) result;

        if (r.isSucceed()) {
            Profile.inst().update(r.getData());
            startActivity(MainActivity.class);
        } else {
            showToast(r.getError());
        }
    }


}
