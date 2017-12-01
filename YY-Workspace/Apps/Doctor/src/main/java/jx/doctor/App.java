package jx.doctor;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;

import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import lib.jg.JAnalyticsStats;
import lib.jg.JG;
import lib.network.NetworkConfig;
import lib.platform.Platform;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.config.ListConfigBuilder;
import lib.ys.config.NavBarConfig;
import lib.ys.stats.Stats;
import lib.ys.util.PackageUtil;
import lib.yy.BaseApp;
import jx.doctor.Constants.PageConstants;
import jx.doctor.network.NetFactory;
import jx.doctor.network.NetworkApiDescriptor;
import jx.doctor.network.UrlUtil;
import jx.doctor.util.CacheUtil;

/**
 * @author yuansui
 * @since 2017/4/5
 */
public class App extends BaseApp {

    /**
     * nav bar定义
     */
    public interface NavBarVal {
        int KHeightDp = 44;
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

        //百度地图
        SDKInitializer.initialize(this);

        // fixme
        Platform.init(this, "21454499cef00", "da83f1b9d28a32e0d57004d58e1eb318");

        // 临时的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        JG.init(this, BuildConfig.DEBUG_LOG);
        Stats.init(new JAnalyticsStats(), BuildConfig.DEBUG_LOG);

        if (BuildConfig.DEBUG_LOG) {
//            LeakCanary.install(this);
        }

//        setMobWeChat();
//        setMobWeChatMoments();
//        setMobWeibo();
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

    protected void setMobWeibo() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id", "1");
        hashMap.put("SortId", "1");
        hashMap.put("AppKey", PackageUtil.getMetaValue("SINA_KEY"));
        hashMap.put("AppSecret", PackageUtil.getMetaValue("SINA_SECRET"));
        hashMap.put("RedirectUrl", "http://www.sharesdk.cn");
        hashMap.put("ShareByAppClient", "true");
        hashMap.put("Enable", "true");
        hashMap.put("ShareByWebApi", "false");
        ShareSDK.setPlatformDevInfo(SinaWeibo.NAME, hashMap);
    }

    protected void setMobWeChat() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id", "2");
        hashMap.put("SortId", "2");
        hashMap.put("AppId", PackageUtil.getMetaValue("WX_ID"));
        hashMap.put("AppSecret", PackageUtil.getMetaValue("WX_SECRET"));
        hashMap.put("BypassApproval", "false");
        hashMap.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(Wechat.NAME, hashMap);
    }

    protected void setMobWeChatMoments() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id", "3");
        hashMap.put("SortId", "");
        hashMap.put("AppId", PackageUtil.getMetaValue("WX_ID"));
        hashMap.put("AppSecret", PackageUtil.getMetaValue("WX_SECRET"));
        hashMap.put("BypassApproval", "false");
        hashMap.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, hashMap);
    }
}
