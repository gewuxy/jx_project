package jx.csp.ui.activity.login;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.model.authorize.PlatformAuthorizeUserInfoManager;
import jx.csp.view.CustomVideoView;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther WangLan
 * @since 2017/9/27
 */

public class ThirdPartyLoginActivity extends BaseActivity {

    private PlatformAuthorizeUserInfoManager mPlatAuth;

    private CustomVideoView mCustomVideoView;

    @Override
    public void initData() {
        mPlatAuth = new PlatformAuthorizeUserInfoManager();
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
          mCustomVideoView = findView(R.id.login_videoview);
    }


    @Override
    public void setViews() {
        setOnClickListener(R.id.layout_login_wechat);
        setOnClickListener(R.id.layout_login_sina);
        setOnClickListener(R.id.layout_login_jx);
        setOnClickListener(R.id.login_mobile);
        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.protocol);

        mCustomVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.media));
        mCustomVideoView.start();
        mCustomVideoView.setOnCompletionListener(mp -> mCustomVideoView.start());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_login_wechat: {
                //Fixme:只是提醒，实际需求没有，记得要删
                showToast("点击微信");
                mPlatAuth.WeiXinAuthorize();
            }
            break;
            case R.id.layout_login_sina: {
                mPlatAuth.QQAuthorize();
            }
            break;
            case R.id.layout_login_jx: {
                startActivity(YaYaAuthorizeLoginActivity.class);
            }
            break;
            case R.id.login_mobile: {
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.login_mail: {
                startActivity(EmailLoginActivity.class);
            }
            break;
            case R.id.protocol: {
                //Fixme:跳转到h5页面，现在还没有文案
                showToast("没有文案，先酱紫，哈哈");
            }
            break;
        }
        finish();
    }
}
