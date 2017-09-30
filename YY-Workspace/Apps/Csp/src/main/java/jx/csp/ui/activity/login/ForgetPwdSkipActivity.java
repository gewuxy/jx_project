package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @auther WangLan
 * @since 2017/9/28
 */

public class ForgetPwdSkipActivity extends BaseActivity implements OnCountDownListener{

    private final int KCountDownTime = 5; // 单位是秒

    private TextView mTvBack;
    private CountDown mCountDown;
    @Override
    public void initData() {
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mCountDown.start(KCountDownTime);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_forget_pwd_skip;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addCloseIcon(bar, getString(R.string.find_pwd), this);
    }

    @Override
    public void findViews() {
        mTvBack = findView(R.id.tv_back_login);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.tv_back_login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back_login:{
                startActivity(EmailLoginActivity.class);
            }
            break;
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        mTvBack.setText(remainCount + getString(R.string.back_to_login));
        if (remainCount == 0) {
            startActivity(EmailLoginActivity.class);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }
}
