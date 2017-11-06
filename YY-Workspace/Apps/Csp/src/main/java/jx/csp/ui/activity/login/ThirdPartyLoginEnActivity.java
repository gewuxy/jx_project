package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.twitter.Twitter;
import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.network.NetworkApiDescriptor.UserAPI;

/**
 * @auther WangLan
 * @since 2017/11/6
 */

public class ThirdPartyLoginEnActivity extends BaseThirdPartyLoginActivity {

    private final int KFaceBookLogin = 1;
    private final int KTwitterLogin = 2;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_en;
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.login_facebook);
        setOnClickListener(R.id.login_twitter);
        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.login_jx);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.login_facebook: {
                Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
                facebook.setPlatformActionListener(new PlatformActionListener() {

                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        showToast("授权成功");
                        PlatformDb platDB = platform.getDb();
                        String userGender = platDB.getUserGender();
                        String icon = platDB.getUserIcon();
                        String userName = platDB.getUserName();
                        String userId = platDB.getUserId();
                        exeNetworkReq(KFaceBookLogin,
                                UserAPI.login(LoginType.facebook_login)
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
                facebook.SSOSetting(false);
                facebook.authorize();
            }
            break;
            case R.id.login_twitter: {
                Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
                twitter.setPlatformActionListener(new PlatformActionListener() {

                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        showToast("授权成功");
                        PlatformDb platDB = platform.getDb();
                        String userGender = platDB.getUserGender();
                        String icon = platDB.getUserIcon();
                        String userName = platDB.getUserName();
                        String userId = platDB.getUserId();
                        exeNetworkReq(KTwitterLogin,
                                UserAPI.login(LoginType.twitter_login)
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
                twitter.SSOSetting(false);
                twitter.authorize();
            }
            break;

        }
    }
}
