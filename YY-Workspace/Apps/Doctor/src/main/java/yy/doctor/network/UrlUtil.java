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
        String KAd = "advert";
        String KLogin = "login";
        String KLogout = "download";
        String KForgetPwd = "regist/pwd/reset";
        String KProfile = "user/info";
        String KModify = "user/modify";
        String KUpHeaderImg = "user/upheadimg";
        String KChangePwd = "user/resetpwd";
        String KCollectionMeetings = "myFavorite";
        String KBindJPush = "bindJpush";
    }

    public interface UrlRegister {
        String KRegisterBase = "regist/";
        String KProvince = KRegisterBase + "provinces";
        String KCity = KRegisterBase + "cities";
        String KRegister = KRegisterBase + "regist";
        String KHospital = KRegisterBase + "hos";
        String KDepart = KRegisterBase + "depart";
    }

    public interface UrlHome {
        String KBanner = "banner";
        String KRecommendMeeting = "meet/tuijian";
        String KRecommendUnitNum = "publicAccount/recommend";
    }

    public interface UrlMeet {
        String KMeetBase = "meet/";
        String KMeets = KMeetBase + "meets";
        String KInfo = KMeetBase + "info";
        String KTypes = KMeetBase + "types";

        String KToExam = KMeetBase + "toexam";
        String KToSurvey = KMeetBase + "tosurvey";
        String KToSign = KMeetBase + "tosign";
        String KToPPT = KMeetBase + "toppt";
        String KToVideo = KMeetBase + "tovideo";

        String KSubmitSur = KMeetBase + "submitsur";
        String KSubmitEx = KMeetBase + "submitex";

        String KSign = KMeetBase + "sign";
        String KSend = KMeetBase + "message/send";
        String KVideo = KMeetBase + "video/sublist";
        String KHistories = KMeetBase + "message/histories";

        String KCollectMeeting = KMeetBase +  "favorite";
    }

    public interface UrlData {
        String KThomsonAll = "data/thomson/all";
        String KThomson = "data/thomson/category";
        String KThomSonData = "data/thomson/datas";
        String KPreview = "data/view";
    }

    public interface UrlEpn {
        String KEpnDetails = "shop/tradeInfo";
        String KEpnRecharge = "alipay/recharge";
    }

    public interface UrlEpc {
        String KExchange = "shop/buy";
        String KOrder = "shop/order";
        String KEpc = "shop/goods";
        String KEpcDetail = "shop/goodInfo";
    }

    public interface UrlUnitNum {
        String KUnitNum = "publicAccount/mySubscribe";
        String KAttention = "publicAccount/subscribe";
        String KUnitNumDetail = "publicAccount/unitInfo";
        String KUnitNumData = "publicAccount/materialList";
    }

    public interface UrlSearch {
        String KSearchRecUnitNum = "publicAccount/recommend";
        String KSearchUnitNum = "publicAccount/search";
        String KSearchMeeting = "meet/search";
    }

}
