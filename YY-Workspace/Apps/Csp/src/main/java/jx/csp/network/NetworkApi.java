package jx.csp.network;

import inject.annotation.network.Api;
import inject.annotation.network.Descriptor;
import inject.annotation.network.Query;
import inject.annotation.network.method.Get;
import inject.annotation.network.method.Post;
import inject.annotation.network.method.Upload;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@Descriptor(
        host = "https://app.medyaya.cn/api/",
        hostDebuggable = "http://59.111.90.245:8084/api/"
//        hostDebuggable = "http://10.0.0.234:8080/api/"   //礼平电脑
//        hostDebuggable = "http://10.0.0.250:8081/api/"   //轩哥电脑
//        hostDebuggable = "https://www.medcn.com/" // yaya 医师授权登录

)
public class NetworkApi {

    @Api
    interface User {

        /**
         * 头像上传
         *
         * @param file
         */
        @Upload("user/updateAvatar")
        void upload(byte[] file);

        /**
         * 修改密码
         *
         * @param old_pwd 旧密码
         * @param new_pwd 新密码
         */
        @Post("user/resetPwd")
        void changePwd(String old_pwd, String new_pwd);

        /**
         * 绑定邮箱
         *
         * @param email 用户名
         */
        @Post("user/toBind/")
        void bindEmail(String email);

        /**
         * 绑定手机
         *
         * @param mobile  手机号
         * @param captcha 验证码
         */
        @Post("user/bindMobile")
        void bindPhone(String mobile, String captcha);

        /**
         * 解绑邮箱或手机
         *
         * @param type 0代表邮箱，1代表手机
         */
        @Post("user/unbind")
        void unBind(int type);

        /**
         * 绑定或解绑第三方账号
         *
         * @param uniqueId     需要绑定的第三方账号唯一标识,,解绑时无此参数
         * @param thirdPartyId 1代表微信，2代表微博，3代表facebook,4代表twitter,5代表YaYa医师,解绑操作只需传递此字段
         * @param nickName     第三方账号的昵称,解绑时无此参数
         * @param gender       性别,解绑时无此参数
         * @param avatar       头像,解绑时无此参数
         */
        @Post("user/changeBindStatus")
        void bindAccountStatus(@Query(opt = true) String uniqueId,
                               @Query(opt = true) int thirdPartyId,
                               @Query(opt = true) String nickName,
                               @Query(opt = true) String gender,
                               @Query(opt = true) String avatar);

    }

    @Api()
    interface Delivery {
        /**
         * 投稿历史
         *
         * @param pageNum
         * @param pageSize
         */
        @Get("delivery/paginate")
        void history(@Query(opt = true) int pageNum,
                     @Query(opt = true) int pageSize);

        /**
         * 投稿
         *
         * @param acceptIds
         * @param courseId
         */
        @Post("delivery/push")
        void unitNum(String acceptIds, int courseId);

        /**
         * 可投稿的单位号
         */
        @Get("delivery/acceptors")
        void contribute();
    }

    /**
     * 广告页
     */
    @Api("advert")
    interface Advert {
        @Post("advert")
        void advert();
    }

    /**
     * 登录
     */
    @Api()
    interface Login {
        /**
         * 登录，包括所有的登录
         *
         * @param thirdPartyId 第三方登录id 1=微信 2=微博 3=Facebook 4=Twitter 5=YaYa医师 6=手机 7=邮箱
         * @param email
         * @param password
         * @param mobile
         * @param captcha
         * @param nickName
         * @param uniqueId     第三方平台唯一的Id
         * @param gender       性别
         * @param country      国家
         * @param province     省份
         * @param city         城市
         * @param district     地区
         * @param avatar       头像
         */
        @Post("user/login")
        void login(int thirdPartyId,
                   @Query(opt = true) String email,
                   @Query(opt = true) String password,
                   @Query(opt = true) String mobile,
                   @Query(opt = true) String captcha,
                   @Query(opt = true) String nickName,
                   @Query(opt = true) String uniqueId,
                   @Query(opt = true) String gender,
                   @Query(opt = true) String country,
                   @Query(opt = true) String province,
                   @Query(opt = true) String city,
                   @Query(opt = true) String district,
                   @Query(opt = true) String avatar);

        /**
         * 获取验证码
         *
         * @param mobile 手机号码
         * @param type   验证码模板类型 0=登录 1=绑定
         */
        @Post("user/sendCaptcha")
        void sendCaptcha(String mobile, String type);

        /**
         * 邮箱注册
         *
         * @param email    邮箱
         * @param password 密码
         * @param nickName 昵称
         */
        @Post("user/register")
        void register(String email, String password, String nickName);

        /**
         * 忘记密码
         *
         * @param email 邮箱
         */
        @Post("email/findPwd")
        void findPwd(String email);
    }

    /**
     * yaya医师授权登录，url也不一样
     */
    @Api()
    interface YaYaAuthorizeLogin {
        @Post("oauth/app/authorize")
        void yayaLogin(String username, String password);
    }

    @Api("meeting")
    interface Meeting {

        /**
         * 进入会议
         *
         * @param courseId
         */
        @Get("join")
        void join(String courseId);

        /**
         * 同步指令
         *
         * @param courseId
         * @param pageNum
         * @param audioUrl
         */
        @Post("sync")
        void sync(@Query (opt = true) String courseId,
                  @Query (opt = true) int pageNum,
                  @Query (opt = true) String audioUrl);

        /**
         * 上传音频
         *
         * @param courseId
         * @param detailId
         * @param playType
         * @param pageNum
         * @param file
         */
        @Upload("upload")
        void uploadAudio(@Query (opt = true) String courseId,
                         @Query (opt = true) String detailId,
                         @Query (opt = true) int playType,
                         @Query (opt = true) int pageNum,
                         @Query (opt = true) byte[] file);
    }

    @Api()
    interface Pay {

        /**
         * Ping++支付
         *
         * @param flux    流量值
         * @param channel 支付方式,按照ping++文档channel属性值给
         */
        @Post("charge/toCharge")
        void pingPay(int flux, String channel);

        /**
         * paypal支付
         *
         * @param flux 流量值
         */
        @Post("charge/createOrder")
        void paypalPay(int flux);

        /**
         * paypal支付结果确认
         *
         * @param paymentId 流量值
         * @param orderId   订单id
         */
        @Post("charge/paypalCallback")
        void paypalPayResult(String paymentId, String orderId);


    }
}
