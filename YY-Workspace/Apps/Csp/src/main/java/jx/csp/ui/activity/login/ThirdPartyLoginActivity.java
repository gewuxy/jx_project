package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.view.CustomVideoView;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;

/**
 * 第三方登录
 *
 * @auther WangLan
 * @since 2017/9/27
 */

public class ThirdPartyLoginActivity extends BaseThirdPartyLoginActivity {

    private final int KWechatLogin = 1;
    private final int KWeiboLogin = 2;
    private final int KLoginVideo = 3;
    private final int KDownLoadVideo = 4;

    private final int KInitVersion = 0; // 首次访问此接口，version= 0
    private final String KFileName = "login_background_video.mp4";

    private CustomVideoView mCustomVideoView;
    private String mUrl;
    private String mLocatePath;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnClickListener(R.id.layout_login_wechat);
        setOnClickListener(R.id.layout_login_sina);
        setOnClickListener(R.id.login_mobile);
        setOnClickListener(R.id.language_transform);
        setOnClickListener(R.id.layout_login_jx);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_login_wechat: {
              /*  mPlatAuth.WeiXinAuthorize();
                exeNetworkReq(KWechatLogin, UserAPI.login(LoginType.wechat_login).uniqueId(mUserId).nickName(mUserName).gender(mUserGender)
                        .avatar(mIcon).build());*/
            }
            break;
            case R.id.layout_login_sina: {
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                sina.setPlatformActionListener(new PlatformActionListener() {

                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        showToast("授权成功");
                        PlatformDb platDB = platform.getDb();
                        String userGender = platDB.getUserGender();
                        String icon = platDB.getUserIcon();
                        String userName = platDB.getUserName();
                        String userId = platDB.getUserId();
                        exeNetworkReq(KWeiboLogin,
                                UserAPI.login(LoginType.weibo_login)
                                        .uniqueId(userId)
                                        .nickName(userName)
                                        .gender(userGender)
                                        .avatar(icon)
                                        .build());
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        showToast("失败");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        showToast("取消");
                    }
                });
                sina.SSOSetting(false);
                sina.authorize();

            }
            break;
            case R.id.login_mobile: {
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.language_transform: {
                startActivity(ThirdPartyLoginEnActivity.class);
            }
            break;
            case R.id.layout_login_jx: {
                startActivity(YaYaAuthorizeLoginActivity.class);
            }
            break;
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }
}
