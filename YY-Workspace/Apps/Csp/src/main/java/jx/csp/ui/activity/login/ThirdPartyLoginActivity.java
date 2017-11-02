package jx.csp.ui.activity.login;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.authorize.PlatformAuthorizeUserInfoManager;
import jx.csp.model.authorize.PlatformAuthorizeUserInfoManagerRouter;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.view.CustomVideoView;
import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * 统一账号登录
 *
 * @auther WangLan
 * @since 2017/9/27
 */
@Route
public class ThirdPartyLoginActivity extends BaseActivity {

    private final int  KWechatLogin = 1;
    private final int  KWeiboLogin = 2;

    private PlatformAuthorizeUserInfoManager mPlatAuth;

    private CustomVideoView mCustomVideoView;
    @Arg
    public String mToken;

    @Arg
    public String mUserGender;

    @Arg
    public String mIcon;

    @Arg
    public String mUserName;

    @Arg
    public String mUserId;


    @Override
    public void initData() {
        mPlatAuth = new PlatformAuthorizeUserInfoManager();
        PlatformAuthorizeUserInfoManagerRouter.create(this);
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
                mPlatAuth.WeiXinAuthorize();
                exeNetworkReq(KWechatLogin,UserAPI.login(LoginType.wechat_login).uniqueId(mUserId).nickName(mUserName).gender(mUserGender)
                        .avatar(mIcon).build());
            }
            break;
            case R.id.layout_login_sina: {
//                mPlatAuth.sinaAuthorize();
                //暂时用qq的
                mPlatAuth.QQAuthorize();
                exeNetworkReq(KWeiboLogin,UserAPI.login(LoginType.weibo_login).uniqueId(mUserId).nickName(mUserName).gender(mUserGender)
                        .avatar(mIcon).build());
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
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();
            startActivity(MainActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }
}
