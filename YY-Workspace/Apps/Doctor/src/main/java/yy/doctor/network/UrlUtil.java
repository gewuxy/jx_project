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
            // 测试线  192.168.1.19:8082/
            mHostName = KHttpDef + "www.medcn.com:8081/v7/";
        } else {
            // 正式线
            mHostName = KHttpDef + "www.medcn.com:8081/v7/";
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
        String logout = "logout";
        String profile = "user/info";
        String modify = "user/modify";
        String upheadimg = "user/upheadimg";
    }

    //TODO:register路径 http://www.medcn.com:8081/v7/api/regist/regist
    public interface UrlRegister {
        String register = "regist/regist";
        String province = "regist/provinces";
        String city = "regist/cities";
    }

    public interface UrlHome {
        String banner = "banner";
    }

    public interface Meeting {

    }

    public interface UrlEpn {
        String epndetails = "shop/tradeInfo";
        String epnrecharge = "alipay/recharge";
    }

    public interface UrlEpc {
        String exchange = "shop/buy";
        String order = "shop/order";
        String epc = "shop/goods";
    }

    public interface UrlUnitNum {
        String unitNum = "publicAccount/mySubscribe";
        String attention = "publicAccount/SubscribeOrNot";
        String unitNumDetail = "publicAccount/findInfo";
    }

    public interface UrlSearch {
        String recommendUnitNum = "publicAccount/recommend";
        String searchUnitNum = "publicAccount/search";
    }

}
