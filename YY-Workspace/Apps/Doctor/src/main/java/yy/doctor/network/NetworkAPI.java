package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.Part;
import inject.annotation.network.Retry;
import inject.annotation.network.Url;
import inject.annotation.network.method.DOWNLOAD_FILE;
import inject.annotation.network.method.POST;
import inject.annotation.network.method.UPLOAD;
import lib.network.model.param.CommonPair;
import lib.ys.util.DeviceUtil;
import yy.doctor.model.Profile;

import static yy.doctor.model.Profile.TProfile.token;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@APIFactory(
        host = "http://app.medyaya.cn/v7/api/",
        hostDebuggable = "http://59.111.90.245:8083/v7/api/"
//        hostDebuggable = "http://10.0.0.234:80/api/" // 礼平电脑
)
public class NetworkAPI {

    public interface Param {
        String KOSVersion = "os_version";
        String KDevice = "os_type";
        String KAppVersion = "app_version";
        String KToken = "token";
    }

    @API
    public interface User {
        /**
         * 登录(绑定微信号)
         *
         * @param username
         * @param password
         * @param openid
         */
        @POST("login")
        void login(String username, String password, String openid);

        /**
         * 登出
         *
         * @param token
         */
        @POST("logout")
        @Retry(count = 5, delay = 1000)
        void logout(String token);

        /**
         * 头像上传
         *
         * @param file
         */
        @UPLOAD("user/update_avatar")
        void upload(byte[] file);
    }

    @API
    public interface Data {
        @DOWNLOAD_FILE
        void download(@Url String url);
    }

    @API("shop")
    public interface Epc {
        /**
         * 商品兑换
         *
         * @param goodsId  商品id
         * @param price    价格
         * @param receiver 收货人
         * @param phone    手机号码
         * @param province 省份
         * @param address  地址
         */
        @POST("buy")
        void exchange(@Part(opt = true) int goodsId,
                      @Part(opt = true) int price,
                      @Part(opt = true) String receiver,
                      @Part(opt = true) String phone,
                      @Part(opt = true) String province,
                      @Part(opt = true) String address);
    }

    public static List<CommonPair> getCommonPairs() {
        List<CommonPair> ps = new ArrayList<>();

        ps.add(newPair(Param.KDevice, "android"));
        ps.add(newPair(Param.KOSVersion, DeviceUtil.getSystemVersion()));
        ps.add(newPair(Param.KAppVersion, DeviceUtil.getAppVersion()));
        if (Profile.inst().isLogin()) {
            ps.add(newPair(Param.KToken, Profile.inst().getString(token)));
        }

        return ps;
    }

    private static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }
}
