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

    public static String getYaYaLogin(){
        return mYaYaAuthorize;
    }

}
