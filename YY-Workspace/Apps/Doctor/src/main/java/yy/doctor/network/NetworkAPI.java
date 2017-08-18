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
//        hostDebuggable = "http://10.0.0.250:8081/"; // 轩哥电脑
//        hostDebuggable = "http://10.0.0.252:8082/"; // 长玲电脑
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

        /**
         * 修改个人资料
         *
         * @param headimg       头像地址
         * @param linkman       真实姓名
         * @param hospital      医院
         * @param hospitalLevel 医院等级
         * @param title         职称
         * @param category      专科一级
         * @param name          专科二级
         * @param province      省份
         * @param city          城市
         * @param zone          区
         */
        @POST("user/modify")
        void modify(@Part(opt = true) String headimg,
                    @Part(opt = true) String linkman,
                    @Part(opt = true) String hospital,
                    @Part(opt = true) String hospitalLevel,
                    @Part(opt = true) String title,
                    @Part(opt = true) String category,
                    @Part(opt = true) String name,
                    @Part(opt = true) String province,
                    @Part(opt = true) String city,
                    @Part(opt = true) String zone);
    }

    @API
    public interface Forget{

        /**
         * 通过邮箱找回密码
         *
         * @param username
         */
        @GET("email/pwd/send_reset_mail")
        void email(String username);

        /**
         * 通过手机找回密码
         *
         * @param mobile
         * @param captcha
         * @param password
         */
        @GET("register/pwd/reset/by_mobile")
        void phone(String mobile, String captcha, String password);
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

        /**
         * 收藏的药品目录详情
         * @param dataFileId
         */
        @GET("data_detail")
        void collectionDetail(String dataFileId);

        /**
         * 搜索药品或临床指南
         *
         * @param keyword
         * @param type
         * @param pageNum
         * @param pageSize
         */
        @POST("data_search")
        void search(String keyword, int type, int pageNum, int pageSize);
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

    @API("register")
    public interface Register{
        /**
         *
         * @param nickname 用户昵称
         * @param linkman  真实姓名
         * @param mobile  手机号
         * @param captcha 验证码
         * @param password  密码
         * @param province  省份
         * @param city  城市
         * @param zone  区县
         * @param hospital  医院名称
         * @param hospitalLevel  hospitalLevel
         * @param category  专科一级名称
         * @param name  专科二级名称
         * @param department  科室名称
         * @param title  职称
         * @param invite  邀请码
         * @param masterId
         */

        @POST("reg")
        void reg(@Part(opt = true) String nickname,
                 @Part(opt = true) String linkman,
                 @Part(opt = true) String mobile,
                 @Part(opt = true) String captcha,
                 @Part(opt = true) String password,
                 @Part(opt = true) String province,
                 @Part(opt = true) String city,
                 @Part(opt = true) String zone,
                 @Part(opt = true) String hospital,
                 @Part(opt = true) Integer hospitalLevel,
                 @Part(opt = true) String category,
                 @Part(opt = true) String name,
                 @Part(opt = true) String department,
                 @Part(opt = true) String title,
                 @Part(opt = true) String invite,
                 @Part(opt = true) String masterId);

        /**
         * 省份
         */
        @GET("provinces")
        void province();

        /**
         * 城市
         * @param preId
         */
        @GET("cities")
        void city(String preId);

        /**
         * 获取验证码
         * @param mobile
         * @param type
         */
        @GET("get_captcha")
        void captcha(String mobile, int type);

        /**
         * 扫一扫
         * @param masterId
         */
        @GET("scan_register")
        void scan(@Part(opt = true) String masterId);

        /**
         *专科
         */
        @GET("specialty")
        void specialty();

        /**
         * 职称
         */
        @GET("title")
        void title();

        /**
         * 配置信息
         * @param version
         */
        @POST("properties")
        void config(int version);
    }

    @API
   public interface Collection{

        /**
         * 收藏或者取消收藏
         *
         * @param resourceId
         * @param type
         * @return
         */
        @GET("set_favorite_status")
        void collectionStatus(String resourceId, @DataType int type);

        /**
         * 收藏的会议列表
         *
         * @param pageNum
         * @param pageSize
         * @return 该值为空或0时，表示会议类型
         */
        @GET("my_favorite")
        void collection(int pageNum, int pageSize, int type);
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
