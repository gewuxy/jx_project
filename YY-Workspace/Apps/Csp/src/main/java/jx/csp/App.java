package jx.csp;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import java.util.Locale;

import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.Constants.PageConstants;
import jx.csp.constant.LangType;
import jx.csp.network.NetFactory;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.network.UrlUtil;
import jx.csp.serv.DownloadServ.DownReqType;
import jx.csp.serv.DownloadServRouter;
import jx.csp.sp.SpApp;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.jg.JAnalyticsStats;
import lib.jg.JG;
import lib.jx.BaseApp;
import lib.network.NetworkConfig;
import lib.platform.Platform;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.config.ListConfigBuilder;
import lib.ys.config.NavBarConfig;
import lib.ys.stats.Stats;
import lib.ys.util.TextUtil;

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
        int KIconSizeDp = 20;
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

        Platform.init(this, "21454499cef00", "da83f1b9d28a32e0d57004d58e1eb318");

        // 临时的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        LangType langType = getLangType();
        // 保存系统语言
        SpApp.inst().saveSystemLang(langType);

        @AppType int appType;  // 国内版 国外版
        if (Util.checkAppCn()) {
            appType = AppType.inland;
        } else {
            appType = AppType.overseas;
        }
        SpApp.inst().setAppType(appType);
        DownloadServRouter.create(DownReqType.login_video, Constants.KVideoUrl, CacheUtil.getVideoLoginFileName())
                .route(this);
    }

    @NonNull
    private LangType getLangType() {
        Locale l = Locale.getDefault();
        LangType langType = LangType.en;
        String language = l.getLanguage();
        /** {@link Locale#CHINESE}*/
        final String zh = "zh";
        if (zh.equals(language)) {
            String script = ConstantsEx.KEmpty;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                String s = l.toString();
                int start = s.indexOf("#");
                if (start != ConstantsEx.KErrNotFound) {
                    int end = s.lastIndexOf("_");
                    if (end > start) {
                        script = s.substring(start + 1, end);
                    } else {
                        script = s.substring(start + 1);
                    }
                }
            } else {
                script = l.getScript();
            }
            if (TextUtil.isEmpty(script)) {
                // 有的地方不返回_#Hant或_#Hans
                final String cn = "CN";
                String country = l.getCountry();
                if (cn.equals(country)) {
                    // 简体中文 , 除了cn其他地方没有区分简体繁体的默认繁体
                    langType = LangType.cn_simplified;
                } else {
                    // 繁体中文
                    langType = LangType.cn;
                }
            } else {
                // Hans表示简体中文 , Hant表示繁体中文 http://www.jianshu.com/p/a6d090234d25
                final String hans = "Hans";
                if (hans.equals(script)) {
                    // 简体中文
                    langType = LangType.cn_simplified;
                } else {
                    // 繁体中文
                    langType = LangType.cn;
                }
            }
        }
        return langType;
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
}
