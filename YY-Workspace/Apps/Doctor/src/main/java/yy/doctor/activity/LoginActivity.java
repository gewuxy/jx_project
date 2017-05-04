package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import lib.network.model.NetworkResponse;
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
            //mTvLogin.setText("fsldfskldf");
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.login_tv: {
                refresh(RefreshWay.dialog);
                exeNetworkRequest(0, NetFactory.login(mEtName.getText().toString(), mEtPwd.getText().toString()));
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
        return JsonParser.ev(nr.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();

        Response<Profile> r = (Response<Profile>) result;
        if (r.isSucceed()) {
            Profile.inst().update(r.getData());
            startActivity(MainActivity.class);
        }
    }


}
