package yaya.csp.model.authorize;

import android.app.Activity;
import android.widget.Toast;

import com.mob.MobSDK;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * @auther WangLan
 * @since 2017/9/26
 */

public class PlatformAuthorizeUserInfoManager {
    private Activity mActivity;
    private MyPlatformActionListener myPlatformActionListener = null;

    public PlatformAuthorizeUserInfoManager(Activity mACt) {
        this.mActivity = mACt;
        this.myPlatformActionListener = new MyPlatformActionListener();
    }

    public void WeiXinAuthorize() {
        Platform weiXin = ShareSDK.getPlatform(Wechat.NAME);
        doAuthorize(weiXin);
    }

    public void sinaAuthorize() {
        Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        doAuthorize(sina);
    }

    public void whatMomentsAuthorize() {
        Platform moments = ShareSDK.getPlatform(WechatMoments.NAME);
        doAuthorize(moments);
    }

    public void wechatFavoriteAuthorize() {
        Platform wechatFavorite = ShareSDK.getPlatform(WechatFavorite.NAME);
        doAuthorize(wechatFavorite);
    }

    public void facebookAuthorize() {
        Platform facebookShare = ShareSDK.getPlatform(Facebook.NAME);
        doAuthorize(facebookShare);
    }

    public void twitterAuthorize() {
        Platform twitterShare = ShareSDK.getPlatform(Twitter.NAME);
        doAuthorize(twitterShare);
    }

    /**
     * 授权的代码
     */
    public void doAuthorize(Platform platform) {
        if (platform != null) {
            platform.setPlatformActionListener(myPlatformActionListener);
            if (platform.isAuthValid()) {
                platform.removeAccount(true);
                return;
            }
            platform.SSOSetting(false);
            platform.authorize();
        }
    }

    /**
     * 授权的代码
     */
    public void doAuthorize(Platform platform, PlatformActionListener listener) {
        if (platform != null) {
            platform.setPlatformActionListener(listener);
            platform.removeAccount(true);
            platform.authorize();
        }
    }

    /**
     * 用户信息的代码
     */
    public void doUserInfo(Platform platform) {
        if (platform != null) {
            platform.showUser(null);
        }
    }

    /**
     *
     * @param platform 平台名称
     * @param shareType 分享类型
     */
    /**
     * 用户信息的代码
     */
    public void doUserInfo(Platform platform, String account) {
        if (platform != null) {
            platform.showUser(account);
        }
    }

    /**
     *
     * @param platform 平台名称
     * @param shareType 分享类型
     */
    /**
     * 用户信息的代码
     */
    public void doUserInfo(Platform platform, String account, PlatformActionListener listener) {
        if (platform != null) {
            platform.setPlatformActionListener(listener);
            platform.showUser(account);
        }
    }

    /**
     * 用户信息的代码
     */
    public void doUserInfo(Platform platform, PlatformActionListener listener) {
        if (platform != null) {
            platform.setPlatformActionListener(listener);
            platform.showUser(null);
        }
    }

    class MyPlatformActionListener implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MobSDK.getContext(), "Authorize Complete.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            throwable.printStackTrace();
            System.out.println("======Authorize Failure=======" + ((platform != null) ? platform.getName() : " ") + ":" + throwable.toString());
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MobSDK.getContext(), "Authorize Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Toast.makeText(MobSDK.getContext(), "Cancel Authorize", Toast.LENGTH_SHORT).show();
        }
    }
}
