package jx.csp.network;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class UrlUtil {

    private static String mHostName = null;

    private static String mBase = null;

    private static boolean mIsDebug = true;

    private static String mYaYaAuthorize = "https://www.medcn.com/oauth/app/authorize";

    private static void init() {
        if (mIsDebug) {
            mHostName = "http://59.111.90.245:8084/" ;
        } else {
            // 正式线
            mHostName = "https://app.medyaya.cn/";
        }

        mBase = mHostName + "api/";
    }

    public static void setDebug(boolean isDebug) {
        mIsDebug = isDebug;
        init();
    }

    public static String getHostName() {
        return mHostName;
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
