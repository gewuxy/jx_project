package jx.csp.network;

import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.Part;
import inject.annotation.network.method.GET;
import inject.annotation.network.method.POST;
import inject.annotation.network.method.UPLOAD;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@APIFactory(
        host = "https://app.medyaya.cn/api/",
        hostDebuggable = "http://59.111.90.245:8084/api/"
//        hostDebuggable = "https://www.medcn.com/" // yaya 医师授权登录

)
public class NetworkAPI {

    @API
    interface User{

        /**
         * 头像上传
         *
         * @param file
         */
        @UPLOAD("user/updateAvatar")
        void upload(byte[] file, String token);

        /**
         * 修改密码
         *
         * @param old_pwd 旧密码
         * @param new_pwd 新密码
         */
        @POST("user/resetPwd")
        void changePwd(String old_pwd, String new_pwd, String token);

        /**
         * 绑定邮箱
         *
         * @param email 用户名
         */
        @POST("user/toBind/")
        void bindEmail(String email, String token);

        /**
         * 绑定手机
         *
         * @param mobile 手机号
         * @param captcha  验证码
         */
        @POST("user/bindMobile")
        void bindPhone(String mobile, String captcha, String token);

        /**
         * 解绑邮箱或手机
         *
         * @param mobile 手机号
         * @param email 邮箱
         */
        @POST("user/unbindEmailOrMobile")
        void unBindEmailOrPhone(@Part(opt = true) String mobile,
                                @Part(opt = true) String email,
                                @Part(opt = true) String token);

        /**
         * 绑定或解绑第三方账号
         *
         * @param uniqueId      需要绑定的第三方账号唯一标识,,解绑时无此参数
         * @param thirdPartyId 1代表微信，2代表微博，3代表facebook,4代表twitter,5代表YaYa医师,解绑操作只需传递此字段
         * @param nickName     第三方账号的昵称,解绑时无此参数
         * @param gender        性别,解绑时无此参数
         * @param avatar        头像,解绑时无此参数
         */
        @POST("user/changeBindStatus")
        void bindAccountStatus(@Part(opt = true) String uniqueId,
                               @Part(opt = true) int thirdPartyId,
                               @Part(opt = true) String nickName,
                               @Part(opt = true) String gender,
                               @Part(opt = true) String avatar,
                               @Part(opt = true) String token);

        /**
         * 投稿历史
         *
         * @param pageNum
         * @param pageSize
         */
        @GET("delivery/paginate")
        void history(@Part(opt = true) int pageNum,
                     @Part(opt = true) int pageSize);

        /**
         * 投稿
         *
         * @param acceptIds
         * @param courseId
         */
        @POST("delivery/push")
        void unitNum(String acceptIds, int courseId);

        /**
         * 可投稿的单位号
         * @param pageNum
         * @param pageSize
         */
        @GET("delivery/acceptors")
        void contribute();


    }

    /**
     * 广告页
     */
    @API("advert")
    interface Advert{
        @POST("advert")
        void advert();
    }

    /**
     * 登录
     */
    @API()
    interface Login{
        /**
         * 登录，包括所有的登录
         * @param thirdPartyId 第三方登录id 1=微信 2=微博 3=Facebook 4=Twitter 5=YaYa医师 6=手机 7=邮箱
         * @param email
         * @param password
         * @param mobile
         * @param captcha
         * @param nickName
         * @param uniqueId 第三方平台唯一的Id
         * @param gender 性别
         * @param country 国家
         * @param province 省份
         * @param city 城市
         * @param district 地区
         * @param avatar 头像
         */
        @POST("user/login")
        void login(int thirdPartyId,
                   @Part(opt = true)String email,
                   @Part(opt = true)String password,
                   @Part(opt = true)String mobile,
                   @Part(opt = true)String captcha,
                   @Part(opt = true)String nickName,
                   @Part(opt = true)String uniqueId,
                   @Part(opt = true)String gender,
                   @Part(opt = true)String country,
                   @Part(opt = true)String province,
                   @Part(opt = true)String city,
                   @Part(opt = true)String district,
                   @Part(opt = true)String avatar);

        /**
         * 获取验证码
         * @param mobile 手机号码
         * @param type 验证码模板类型 0=登录 1=绑定
         * @param token 没有登录的时候需要token值，只是测试时候，实际文档没有
         */
        @POST("user/sendCaptcha")
        void sendCaptcha(String mobile, String type, String token);

        /**
         * 邮箱注册
         * @param email 邮箱
         * @param password 密码
         * @param nickName 昵称
         */
        @POST("user/register")
        void register(String email,String password,String nickName);

        /**
         * 忘记密码
         * @param email 邮箱
         */
        @POST("email/findPwd")
        void findPwd(String email);
    }

    /**
     * yaya医师授权登录，url也不一样
     */
    @API()
    interface YaYaAuthorizeLogin{
        @POST("oauth/app/authorize")
        void yayaLogin(String username,String password);
    }

}
