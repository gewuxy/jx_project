package yaya.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;

import cn.sharesdk.framework.Platform;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseActivity;
import yaya.csp.R;
import yaya.csp.model.authorize.PlatformAuthorizeUserInfoManager;

/**
 * @auther WangLan
 * @since 2017/9/21
 */

public class ThirdPartyLoginActivity extends BaseActivity implements OnClickListener{
    private TextView mAgreeLogin;
    private Platform mPlatForm;
    private PlatformAuthorizeUserInfoManager mPlatAuth;
    @Override
    public void initData() {
        MobSDK.init(this,"20363f4ed7f9e","38d54e92bac9c9367d249186c53ad89c");
        mPlatAuth = new PlatformAuthorizeUserInfoManager(this);
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
        mAgreeLogin = findView(R.id.agree_login);
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

        SpannableString s = new SpannableString(getString(R.string.agree_login));
        s.setSpan(new ForegroundColorSpan(ResLoader.getColor(R.color.text_ff167)), 9,s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mAgreeLogin.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_wechat:{
                Toast.makeText(this,"点击微信",Toast.LENGTH_SHORT).show();
                mPlatAuth.WeiXinAuthorize();
                mPlatAuth.doAuthorize(mPlatForm);
            }
            break;
            case R.id.login_sina:{
                mPlatAuth.sinaAuthorize();
                mPlatAuth.doAuthorize(mPlatForm);
            }
            break;
            case R.id.login_yaya:{

            }
            break;
            case R.id.login_phone:{
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.login_email:{
                startActivity(EmailLoginActivity.class);
            }
            break;
            case R.id.login_facebook:{
                mPlatAuth.facebookAuthorize();
                mPlatAuth.doAuthorize(mPlatForm);
            }
            break;
            case R.id.login_twitter:{
                mPlatAuth.twitterAuthorize();
                mPlatAuth.doAuthorize(mPlatForm);
            }
            break;
        }
    }

}
