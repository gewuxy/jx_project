package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/19
 */
public class LoginActivity extends BaseActivity {

    private TextView mTvLogin;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public void findViews() {

        mTvLogin=findView(R.id.login_tv);

    }

    @Override
    public void setViewsValue() {

        mTvLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id=v.getId();
        switch (id){
            case R.id.login_tv: {
                startActivity(RegisterActivity.class);
            }
            break;
        }
    }
}
