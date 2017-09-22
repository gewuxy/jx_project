package yaya.csp.network;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class UrlUtil {

    private static String mHostName = null;
    private static String mBaseHost = null;

    private static String mBase = null;

    private static boolean mIsDebug = true;

    private static void init() {
        if (mIsDebug) {
            mHostName = "http://59.111.90.245:8084/api/";
        } else {
            // 正式线
            mBaseHost = "app.medyaya.cn/";
            mHostName = "https://" + mBaseHost;
        }

        mBase = mHostName;
        mBaseHost += "api/";
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

}
