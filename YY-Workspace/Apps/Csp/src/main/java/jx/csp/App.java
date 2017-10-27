package jx.csp;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.mob.MobSDK;

import java.util.HashMap;
import java.util.Locale;

import jx.csp.Constants.PageConstants;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.line.Line;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import jx.csp.network.NetFactory;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpApp;
import jx.csp.util.CacheUtil;
import lib.jg.JAnalyticsStats;
import lib.jg.JG;
import lib.network.NetworkConfig;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.config.ListConfigBuilder;
import lib.ys.config.NavBarConfig;
import lib.ys.stats.Stats;
import lib.ys.util.DeviceUtil;
import lib.ys.util.PackageUtil;
import lib.yy.BaseApp;

/**
 * @auther yuansui
 * @since 2017/9/20
 */

public class App extends BaseApp {

    /**
     * nav bar定义
     */
    public interface NavBarVal {
        int KHeightDp = 48;
        int KIconSizeDp = 16;
        int KIconPaddingHorizontalDp = 12;
        int KTextMarginHorizontalDp = 12;
        int KLeftTextSizeDp = 14;
        int KMidTextSizeDp = 17;
        int KRightTextSizeDp = 14;
    }

    @Override
    protected AppConfig configureApp() {
        return AppConfig.newBuilder()
                .bgColorRes(R.color.app_bg)
                .enableFlatBar(false)
                .initRefreshWay(RefreshWay.embed)
                .enableSwipeFinish(BuildConfig.SWIPE_BACK_ENABLE)
                .build();
    }

    @Override
    protected NetworkConfig configureNetwork() {
        return NetworkConfig.newBuilder()
                .connectTimeout(KTimeout)
                .readTimeout(KTimeout)
                .writeTimeout(KTimeout)
                .headersMaker(() -> NetFactory.getBaseHeader())
                .timeoutToast(getString(R.string.connect_timeout))
                .disconnectToast(getString(R.string.network_disabled))
                .cacheDir(CacheUtil.getUploadCacheDir())
                .build();
    }

    @Override
    protected NavBarConfig configureNavBar() {
        return NavBarConfig.newBuilder()
                .heightDp(NavBarVal.KHeightDp)
                .bgColorRes(R.color.app_nav_bar_bg)
                .iconPaddingHorizontalDp(NavBarVal.KIconPaddingHorizontalDp)
                .iconSizeDp(NavBarVal.KIconSizeDp)
                .textColorRes(R.color.nav_bar_text_selector)
                .textMarginHorizontalDp(NavBarVal.KTextMarginHorizontalDp)
                .textSizeLeftDp(NavBarVal.KLeftTextSizeDp)
                .textSizeMidDp(NavBarVal.KMidTextSizeDp)
                .textSizeRightDp(NavBarVal.KRightTextSizeDp)
                .focusBgDrawableRes(R.drawable.nav_bar_selector)
                .build();
    }

    @Override
    protected ListConfig configureList() {
        return ListConfigBuilder.create()
                .type(PageDownType.page)
                .initOffset(PageConstants.KPage)
                .limit(PageConstants.KPageSize)
                .build();
    }

    @Override
    protected void init() {
        // log
        YSLog.setDebugState(BuildConfig.DEBUG_LOG);

        UrlUtil.setDebug(BuildConfig.DEBUG_NETWORK);
        NetworkApiDescriptor.setDebuggable(BuildConfig.DEBUG_NETWORK);

        JG.init(this, BuildConfig.DEBUG_LOG);
        Stats.init(new JAnalyticsStats(), BuildConfig.DEBUG_LOG);
        MobSDK.init(this,"21454499cef00","da83f1b9d28a32e0d57004d58e1eb318");

        String language = PackageUtil.getMetaValue("JX_LANGUAGE");
        if (language.equals("en")) {
            DeviceUtil.setResLocale(this, Locale.ENGLISH);
        } else {
            DeviceUtil.setResLocale(this, Locale.getDefault());
        }
        // 保存系统语言
        SpApp.inst().saveSystemLanguage(Locale.getDefault().getLanguage());
        // 保存国家 为了区分简繁体
        SpApp.inst().saveCountry(Locale.getDefault().getCountry());
//        setMobWeibo();
        setMobWeChat();
        setMobQQ();
        setMobTwitter();
        setMobLinkedIn();
        setMobLine();
        setMobWeChatMoments();
        setMobQZone();
    }

    @Override
    protected String getNetworkImageCacheDir() {
        return CacheUtil.getBmpCacheDir();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //微博的Mob还没集成进来
   /* protected void setMobWeibo(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","1");
        hashMap.put("SortId","1");
        hashMap.put("AppKey","2989630614");
        hashMap.put("AppSecret","e3aab4c005032d74fe76b5ab3d4effb8");
        hashMap.put("RedirectUrl","http://www.sharesdk.cn");
        hashMap.put("ShareByAppClient","true");
        hashMap.put("Enable","true");
        hashMap.put("ShareByWebApi","false");
        ShareSDK.setPlatformDevInfo(SinaWeibo.Name,hashMap);
    }*/

    protected void setMobQQ(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","2");
        hashMap.put("SortId","2");
        hashMap.put("AppKey","96ivtWERQz1SZ8QB");
        hashMap.put("AppId","1106485170");
        hashMap.put("BypassApproval","false");
        hashMap.put("ShareByAppClient","true");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(QQ.NAME,hashMap);
    }

    protected void setMobWeChat(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","3");
        hashMap.put("SortId","3");
        hashMap.put("AppId","wx142cb3956210ccd3");
        hashMap.put("AppSecret","bc67cd82b0656e85f9a924f7679d75f7");
        hashMap.put("BypassApproval","false");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(Wechat.NAME,hashMap);
    }

    protected void setMobTwitter(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","4");
        hashMap.put("SortId","4");
        hashMap.put("ConsumerKey","hqhT16vzm2ZJxJary0WOULGfv");
        hashMap.put("ConsumerSecret","apMjl5Y1c3PxlyEsa4wJNcqNem7Yl5xtjv2TIb3WjBotUpB1Fk");
        hashMap.put("CallbackUrl","http://mob.com");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(Twitter.NAME,hashMap);
    }

    protected void setMobLinkedIn(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","5");
        hashMap.put("SortId","5");
        hashMap.put("ApiKey","81143jcz89ac7k");
        hashMap.put("SecretKey","Xs3H202AB9tyJpu5");
        hashMap.put("RedirectUrl","http://sharesdk.cn");
        hashMap.put("ShareByAppClient","true");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(LinkedIn.NAME,hashMap);
    }

    protected void setMobLine(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","6");
        hashMap.put("SortId","6");
        hashMap.put("ChannelID","1541221136");
        hashMap.put("ChannelSecret","8ae3221a628add950f2422dd8e76f0a9");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(Line.NAME,hashMap);
    }

    protected void setMobWeChatMoments(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","7");
        hashMap.put("SortId","7");
        hashMap.put("AppId","wx142cb3956210ccd3");
        hashMap.put("AppSecret","bc67cd82b0656e85f9a924f7679d75f7");
        hashMap.put("BypassApproval","false");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME,hashMap);
    }

    protected void setMobQZone(){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id","8");
        hashMap.put("SortId","8");
        hashMap.put("AppKey","96ivtWERQz1SZ8QB");
        hashMap.put("AppId","1106485170");
        hashMap.put("BypassApproval","false");
        hashMap.put("Enable","true");
        ShareSDK.setPlatformDevInfo(QQ.NAME,hashMap);
    }

}
