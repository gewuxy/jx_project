package yy.doctor.network;

import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.Part;
import inject.annotation.network.Retry;
import inject.annotation.network.Url;
import inject.annotation.network.method.DOWNLOAD_FILE;
import inject.annotation.network.method.GET;
import inject.annotation.network.method.POST;
import inject.annotation.network.method.UPLOAD;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@APIFactory(
        host = "http://app.medyaya.cn/v7/api/",
        hostDebuggable = "http://59.111.90.245:8083/v7/api/"
//        hostDebuggable = "http://10.0.0.234:80/api/" // 礼平电脑
//        hostDebuggable = "http://10.0.0.250:8081/api/"; // 轩哥电脑
//        hostDebuggable = "http://10.0.0.252:8082/api/"; // 长玲电脑
)
public class NetworkAPI {

    @API
    interface User {
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
    interface Forget {

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
    interface Home {

        /**
         * 首页banner
         */
        @GET("banner")
        void banner();

        /**
         * 首页推荐会议(含文件夹)
         */
        @GET("meet/recommend/meet/folder")
        void recommendMeeting(int page, int pageSize);
    }

    @API("data")
    interface Data {
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
         * 搜索药品或临床指南
         *
         * @param keyword  搜索关键字
         * @param type
         * @param pageNum
         * @param pageSize
         */
        @POST("data_search")
        void search(String keyword, @DataType int type, int pageNum, int pageSize);
    }

    @API("Account")
    interface UnitNum {
        /**
         * 首页推荐单位号
         */
        @GET("recommend")
        void recommendUnitNum();

        /**
         * 关注的单位号
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
        void attention(int masterId, int status);

    }

    @API("shop")
    interface Epc {

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
}
