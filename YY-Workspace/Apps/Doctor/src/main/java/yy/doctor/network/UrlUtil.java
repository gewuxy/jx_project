package yy.doctor.network;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class UrlUtil {

    private static String KHttpDef = "https://";
    private static String mHostName = null;
    private static String mBaseHost = null;

    protected static String mBase = null;

    private static boolean mIsDebug = true;

    private static void init() {
        if (mIsDebug) {
            // 测试线
            mBaseHost = "59.111.90.245:8083/v7/";
//            mBaseHost = "10.0.0.234:80/api/"; // 礼平电脑
//            mBaseHost = "10.0.0.250:8081/"; // 轩哥电脑
//            mBaseHost = "10.0.0.252:8082/"; // 长玲电脑
        } else {
            // 正式线
            mBaseHost = "app.medyaya.cn/";
        }

        mHostName = KHttpDef + mBaseHost;
        mBase = mHostName + "api/";
        mBaseHost += "api/";
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

    public static String getBaseHost() {
        return mBaseHost;
    }

    public interface UrlUser {
        String KModify = "user/modify";
    }

    public interface UrlMeet {
        String KMeetBase = "meet/";
        String KMeetShare = KMeetBase + "share?meetId=";
        String KWs = "ws://";
        String KIm = "/im";
    }

}
