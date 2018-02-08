package jx.csp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import java.util.Locale;

import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.Constants.PageConstants;
import jx.csp.constant.LangType;
import jx.csp.model.Profile;
import jx.csp.network.NetFactory;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.login.auth.AuthLoginActivity;
import jx.csp.ui.activity.login.auth.AuthLoginOverseaActivity;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.jg.JAnalyticsStats;
import lib.jg.JG;
import lib.jx.BaseApp;
import lib.network.NetworkConfig;
import lib.network.model.interfaces.IResult;
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
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.interfaces.listener.onInterceptNetListener;
import lib.ys.util.LaunchUtil;
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
                .listener(new AccountFrozen())
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
                .headersMaker(NetFactory::getBaseHeader)
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LangType langType = getLangType();
        // 保存系统语言
        SpApp.inst().saveSystemLang(langType);
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
    }

    /**
     * {@link Locale#CHINESE}
     */
    @NonNull
    private LangType getLangType() {
        Locale l = Locale.getDefault();
        LangType langType = LangType.cn_simplified;
        String language = l.getLanguage();
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

    /**
     * 账号冻结
     */
    public static class AccountFrozen implements onInterceptNetListener {

        @Override
        public boolean onIntercept(IResult r, Object... o) {
            if (r != null && r.getCode() == Constants.KAccountFrozen) {
                Intent intent;
                if (Util.checkAppCn()) {
                    intent = new Intent(getContext(), AuthLoginActivity.class);
                } else {
                    intent = new Intent(getContext(), AuthLoginOverseaActivity.class);
                }
                intent.putExtra(Constants.KData, r.getError().getMessage());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                LaunchUtil.startActivity(intent);
                Profile.inst().clear();
                if (o != null) {
                    Activity a = null;
                    if (o[0] instanceof Activity) {
                        a = (Activity) o[0];
                    } else if (o[0] instanceof android.app.Fragment) {
                        android.app.Fragment f = (android.app.Fragment) o[0];
                        a = f.getActivity();
                    } else if (o[0] instanceof android.support.v4.app.Fragment) {
                        android.support.v4.app.Fragment f = (android.support.v4.app.Fragment) o[0];
                        a = f.getActivity();
                    }
                    if (a != null) {
                        if (a instanceof ActivityEx) {
                            ActivityEx aEx = (ActivityEx) a;
                            aEx.stopRefresh(); // 防止泄露
                        }
                        a.finish();
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
