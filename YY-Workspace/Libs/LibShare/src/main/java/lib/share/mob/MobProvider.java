package lib.share.mob;

import android.media.Image;

import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.line.Line;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import lib.share.BaseProvider;

/**
 * @auther WangLan
 * @since 2017/10/31
 */

public class MobProvider extends BaseProvider {

    @Override
    public void setQQ(String key, String secret) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Id", "2");
        hashMap.put("SortId", "2");
        hashMap.put("AppKey", key);
        hashMap.put("AppId", secret);
        hashMap.put("BypassApproval", "false");
        hashMap.put("ShareByAppClient", "true");
        hashMap.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(QQ.NAME, hashMap);
    }

    @Override
    public void setWeibo(String key, String secret) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","1");
        hashMap.put("SortId","1");
        hashMap.put("AppKey", key);
        hashMap.put("AppSecret",secret);
        hashMap.put("RedirectUrl","http://www.sharesdk.cn");
        hashMap.put("ShareByAppClient","true");
        hashMap.put("Enable","true");
        hashMap.put("ShareByWebApi","false");
        ShareSDK.setPlatformDevInfo(SinaWeibo.NAME,hashMap);
    }

    @Override
    public void setWechat(String key, String secret) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","3");
        hashMap.put("SortId","3");
        hashMap.put("AppId",key);
        hashMap.put("AppSecret",secret);
        hashMap.put("BypassApproval","false");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(Wechat.NAME,hashMap);
    }

    @Override
    public void setTwitter(String key, String secret) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","4");
        hashMap.put("SortId","4");
        hashMap.put("ConsumerKey",key);
        hashMap.put("ConsumerSecret",secret);
        hashMap.put("CallbackUrl","http://mob.com");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(Twitter.NAME,hashMap);
    }

    @Override
    public void setLinkedIn(String key, String secret) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","5");
        hashMap.put("SortId","5");
        hashMap.put("ApiKey",key);
        hashMap.put("SecretKey",secret);
        hashMap.put("RedirectUrl","http://sharesdk.cn");
        hashMap.put("ShareByAppClient","true");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(LinkedIn.NAME,hashMap);
    }

    @Override
    public void setLine(String key, String secret) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","6");
        hashMap.put("SortId","6");
        hashMap.put("ChannelID",key);
        hashMap.put("ChannelSecret",secret);
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(Line.NAME,hashMap);
    }

    @Override
    public void setWeChatMoments(String key, String secret) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","7");
        hashMap.put("SortId","7");
        hashMap.put("AppId",key);
        hashMap.put("AppSecret",secret);
        hashMap.put("BypassApproval","false");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME,hashMap);
    }

    @Override
    public void setQZone(String key, String secret) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","8");
        hashMap.put("SortId","8");
        hashMap.put("AppKey",key);
        hashMap.put("AppId",secret);
        hashMap.put("BypassApproval","false");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(QQ.NAME,hashMap);
    }


    @Override
    public void share(String platform, String text) {
    }

    @Override
    public void share(String platform, String text, Image image) {

    }
}
