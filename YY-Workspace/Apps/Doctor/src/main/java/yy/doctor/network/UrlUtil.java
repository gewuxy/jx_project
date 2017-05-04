package yy.doctor.network;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class UrlUtil {

    private static String KHttpDef = "http://";
    private static String mHostName = null;

    protected static String mBase = null;

    private static boolean mIsDebug = true;

    private static void init() {
        if (mIsDebug) {
            // 测试线
            mHostName = KHttpDef + "appserver:port/v7/";
        } else {
            // 正式线
            mHostName = KHttpDef + "appserver:port/v7/";
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

    public interface UrlMain {
        String KTttttt = "";
    }

    public interface UrlUser {
        String login = "login";
        String logout = "";
        String profile = "";
    }

    public interface Meeting {

    }
}
