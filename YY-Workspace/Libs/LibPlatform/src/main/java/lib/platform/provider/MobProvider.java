package lib.platform.provider;

import android.content.Context;

import com.mob.MobSDK;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.line.Line;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.listener.OnShareListener;
import lib.platform.model.AuthParams;
import lib.platform.model.ShareParams;
import lib.ys.YSLog;
import lib.ys.util.PackageUtil;

/**
 * @auther yuansui
 * @since 2017/11/6
 */
public class MobProvider implements Provider {

    public MobProvider(Context context, String key, String secret) {
        MobSDK.init(context, key, secret);

        init(Type.sina, getVal("SINA_KEY"), getVal("SINA_SECRET"));
        init(Type.wechat, getVal("WX_ID"), getVal("WX_SECRET"));
        init(Type.qq, getVal("QQ_ID"), getVal("QQ_SECRET"));
        init(Type.twitter, getVal("TWITTER_KEY"), getVal("TWITTER_SECRET"));
        init(Type.linkedin, getVal("LINKIN_APIKEY"), getVal("LINKIN_SECRETKEY"));
        init(Type.line, getVal("LINE_ID"), getVal("LINE_SECRET"));
    }

    private String getVal(String key) {
        return PackageUtil.getMetaValue(key);
    }

    @Override
    public void init(Type p, String key, String secret) {
        YSLog.d("www", "init: " + p.name());
        YSLog.d("www", "init: key = " + key);
        YSLog.d("www", "init: s = " + secret);
        HashMap<String, Object> hashMap = new HashMap<>();

        String id = null;
        String name = null;
        switch (p) {
            case sina: {
                name = SinaWeibo.NAME;
                id = "1";
                hashMap.put("AppKey", key);
                hashMap.put("AppSecret", secret);
                hashMap.put("RedirectUrl", "http://medcn.synology.me:8889/mgr/oauth/callback?thirdPartyId=2");
                hashMap.put("ShareByAppClient", "true");
                hashMap.put("ShareByWebApi", "false");
            }
            break;
            case qq: {
                name = QQ.NAME;
                id = "2";
                hashMap.put("AppKey", key);
                hashMap.put("AppId", secret);
                hashMap.put("ShareByAppClient", "true");
            }
            break;
            case wechat: {
                name = Wechat.NAME;
                id = "3";
                hashMap.put("AppId", key);
                hashMap.put("AppSecret", secret);
            }
            break;
            case twitter: {
                name = Twitter.NAME;
                id = "4";
                hashMap.put("ConsumerKey", key);
                hashMap.put("ConsumerSecret", secret);
                hashMap.put("CallbackUrl", "http://mob.com");
            }
            break;
            case linkedin: {
                name = LinkedIn.NAME;
                id = "5";
            }
            break;
            case line: {
                name = Line.NAME;
                id = "6";
            }
            break;
            case wechat_friend: {
                name = WechatMoments.NAME;
                id = "7";
                hashMap.put("AppId", key);
                hashMap.put("AppSecret", secret);
            }
            break;
            case qzone: {
                name = QZone.NAME;
                id = "8";
            }
            break;
        }

        hashMap.put("Id", id);
        hashMap.put("SortId", id);
        hashMap.put("BypassApproval", "false");
        hashMap.put("Enable", "true");

        ShareSDK.setPlatformDevInfo(name, hashMap);
    }

    @Override
    public void auth(Type type, OnAuthListener l) {
        cn.sharesdk.framework.Platform p = null;
        switch (type) {
            case sina: {
                p = ShareSDK.getPlatform(SinaWeibo.NAME);
            }
            break;
            case facebook: {
                p = ShareSDK.getPlatform(Facebook.NAME);
            }
            break;
            case wechat: {
                p = ShareSDK.getPlatform(Wechat.NAME);
            }
            break;
            case wechat_friend: {
                p = ShareSDK.getPlatform(WechatMoments.NAME);
            }
            break;
            case line: {
                p = ShareSDK.getPlatform(Line.NAME);
            }
            break;
            case linkedin: {
                p = ShareSDK.getPlatform(LinkedIn.NAME);
            }
            break;
            case qq: {
                p = ShareSDK.getPlatform(QQ.NAME);
            }
            break;
            case qzone: {
                p = ShareSDK.getPlatform(QZone.NAME);
            }
            break;
            case twitter: {
                p = ShareSDK.getPlatform(Twitter.NAME);
            }
            break;
            case sms: {
                p = ShareSDK.getPlatform(ShortMessage.NAME);
            }
            break;
        }

        if (p == null) {
            return;
        }

        p.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onComplete(cn.sharesdk.framework.Platform platform, int i, HashMap<String, Object> hashMap) {
                if (l != null) {
                    PlatformDb db = platform.getDb();
                    l.onAuthSuccess(AuthParams.newBuilder()
                            .gender(db.getUserGender())
                            .icon(db.getUserIcon())
                            .id(db.getUserId())
                            .name(db.getUserName())
                            .build());
                }
            }

            @Override
            public void onError(cn.sharesdk.framework.Platform platform, int i, Throwable throwable) {
                if (l != null) {
                    l.onAuthError(throwable.getMessage());
                }
            }

            @Override
            public void onCancel(cn.sharesdk.framework.Platform platform, int i) {
                if (l != null) {
                    l.onAuthCancel();
                }
            }
        });

        p.SSOSetting(false);
        p.authorize();
    }

    @Override
    public void share(Type type, ShareParams param, OnShareListener l) {
        cn.sharesdk.framework.Platform p = null;

        cn.sharesdk.framework.Platform.ShareParams shareParams = new cn.sharesdk.framework.Platform.ShareParams();
        switch (type) {
            case wechat_friend: {
                p = ShareSDK.getPlatform(WechatMoments.NAME);
                shareParams.setShareType(cn.sharesdk.framework.Platform.SHARE_WEBPAGE);
            }
            break;
            case wechat: {
                p = ShareSDK.getPlatform(Wechat.NAME);
                shareParams.setShareType(cn.sharesdk.framework.Platform.SHARE_WEBPAGE);
            }
            break;
        }

        p.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onComplete(cn.sharesdk.framework.Platform platform, int i, HashMap<String, Object> hashMap) {
                if (l != null) {
                    l.onShareSuccess();
                }
            }

            @Override
            public void onError(cn.sharesdk.framework.Platform platform, int i, Throwable throwable) {
                if (l != null) {
                    l.onShareError(throwable.getMessage());
                }
            }

            @Override
            public void onCancel(cn.sharesdk.framework.Platform platform, int i) {
                if (l != null) {
                    l.onShareCancel();
                }
            }
        });

        p.share(shareParams);

    }
}
