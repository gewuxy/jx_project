package jx.csp.network;

import inject.annotation.network.Descriptor;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class UrlUtil {

    private static String mBase = null;

    private static boolean mIsDebug = true;

    private static String mYaYaAuthorize = "https://www.medcn.com/oauth/app/authorize";

    private static String mBaseHttp = " http://www.cspmeeting.com/view/";
    //更新日志
    private static String mUrlUpdateLog = mBaseHttp + "17110216044331146598";
    //免责声明  服务协议
    private static String mUrlDisclaimer = mBaseHttp + "17110215475385132976";
    //帮助
    private static String mUrlHelp = mBaseHttp + "17110216023876150654";
    //关于我们
    private static String mUrlAboutUs = mBaseHttp + "17110216051754139182";

    private static void init() {
        Descriptor des = NetworkApi.class.getAnnotation(Descriptor.class);
        mBase = mIsDebug ? des.hostDebuggable() : des.host();
    }

    public static void setDebug(boolean isDebug) {
        mIsDebug = isDebug;
        init();
    }

    public static String getBaseUrl() {
        return mBase;
    }

    public interface UrlUser {
        String KModify = "user/updateInfo";
    }

    public static String getYaYaLogin() {
        return mYaYaAuthorize;
    }

    public static String getUrlUpdateLog() {
        return mUrlUpdateLog;
    }

    public static String getUrlHelp() {
        return mUrlHelp;
    }

    public static String getUrlDisclaimer() {
        return mUrlDisclaimer;
    }

    public static String getUrlAboutUs() {
        return mUrlAboutUs;
    }

}
