package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.Part;
import inject.annotation.network.Retry;
import inject.annotation.network.Url;
import inject.annotation.network.method.DOWNLOAD_FILE;
import inject.annotation.network.method.GET;
import inject.annotation.network.method.POST;
import inject.annotation.network.method.UPLOAD;
import lib.network.model.param.CommonPair;
import lib.ys.util.DeviceUtil;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;

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
    public interface Home {

        /**
         * 首页banner
         *
         */
        @GET("banner")
        void banner();

        /**
         * 首页推荐会议(含文件夹)
         *
         */
        @GET("meet/recommend/meet/folder")
        void recommendMeeting(int page, int pageSize);

        /**
         * 首页推荐单位号
         *
         */
        @GET("publicAccount/recommend")
        void recommendUnitNum();
    }

    @API("data")
    public interface Data {
        @DOWNLOAD_FILE
        void download(@Url String url);

        /**
         * 药品目录文件或文件夹列表
         *
         * @param preId    父级id,第一级不用传preId
         * @param type     {@link yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType}
         * @param leaf     下一级是否是文件夹, 下一级为文件返回true,下一级是文件夹返回false.第一级传null或空字符串
         * @param pageNum  当前页数
         * @param pageSize 显示条数
         * @return
         */
        @GET("data_category")
        void units(String preId, int type, boolean leaf, int pageNum, int pageSize);
    }

    @API("publicAccount")
    public interface UnitNum {

        /**
         * 关注的单位号
         *
         */
        @GET("mySubscribe")
        void attentionUnitNum();

        /**
         * 单位号详情
         *
         * @param id
         * @param pageNum
         * @param pageSize
         */
        @GET("unitInfo")
        void unitNumDetail(int id, int pageNum, int pageSize);

        /**
         * 单位号资料列表
         *
         * @param id
         * @param pageNum
         * @param pageSize
         */
        @GET("materialList")
        void unitNumData(String id, int pageNum, int pageSize);

        /**
         * 关注单位号 取消关注
         *
         * @param masterId
         * @param status   0:取消关注 1：关注
         */
        @GET("subscribe")
        void attention(int masterId, int status) ;

    }

    @API("shop")
    public interface Epc {

        /**
         * 象城
         *
         * @param pageNum
         * @param pageSize
         */
        @GET("goods")
        void epc(int pageNum, int pageSize);

        /**
         * 商品详情
         *
         * @param id
         */
        @GET("goodInfo")
        void epcDetail(int id);

        /**
         * 订单
         *
         * @param pageNum
         */
        @GET("order")
        void order(int pageNum, int pageSize);

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
            ps.add(newPair(Param.KToken, Profile.inst().getString(TProfile.token)));
        }

        return ps;
    }

    private static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }
}
