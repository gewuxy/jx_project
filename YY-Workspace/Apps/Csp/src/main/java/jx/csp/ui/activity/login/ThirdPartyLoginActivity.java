package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import jx.csp.R;
import jx.csp.model.authorize.PlatformAuthorizeUserInfoManager;
import jx.csp.ui.activity.TestActivity;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther WangLan
 * @since 2017/9/27
 */

public class ThirdpartyLoginActivity extends BaseActivity {

    private PlatformAuthorizeUserInfoManager mPlatAuth;

    @Override
    public void initData() {
        mPlatAuth = new PlatformAuthorizeUserInfoManager();
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_third_party_login;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
    }


    @Override
    public void setViews() {
        setOnClickListener(R.id.login_wechat);
        setOnClickListener(R.id.login_sina);
        setOnClickListener(R.id.login_yaya);
        setOnClickListener(R.id.login_phone);
        setOnClickListener(R.id.login_email);
        setOnClickListener(R.id.login_facebook);
        setOnClickListener(R.id.login_twitter);
        setOnClickListener(R.id.protocol);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_wechat: {
                Toast.makeText(this, "点击微信", Toast.LENGTH_SHORT).show();
                mPlatAuth.WeiXinAuthorize();
            }
            break;
            case R.id.login_sina: {
                mPlatAuth.sinaAuthorize();
            }
            break;
            case R.id.login_yaya: {

            }
            break;
            case R.id.login_phone: {
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.login_email: {
                startActivity(EmailLoginActivity.class);
            }
            break;
            case R.id.login_facebook: {
                mPlatAuth.facebookAuthorize();
            }
            break;
            case R.id.login_twitter: {
                mPlatAuth.twitterAuthorize();
            }
            break;
            case R.id.protocol: {
                //Fixme:跳转到h5页面，现在还没有文案
               startActivity(TestActivity.class);
            }
            break;
        }
    }
}
