package jx.csp.network;

import inject.annotation.network.Api;
import inject.annotation.network.Descriptor;
import inject.annotation.network.Query;
import inject.annotation.network.Retry;
import inject.annotation.network.Url;
import inject.annotation.network.method.DownloadFile;
import inject.annotation.network.method.Get;
import inject.annotation.network.method.Post;
import inject.annotation.network.method.Upload;
import jx.csp.constant.BindId;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@Descriptor(
        host = "https://app.medyaya.cn/api/",
        hostDebuggable = "http://59.111.90.245:8084/api/"
//        hostDebuggable = "http://10.0.0.234:8080/api/"   // 礼平电脑
//        hostDebuggable = "http://10.0.0.250:8081/api/"   // 轩哥电脑
//        hostDebuggable = "http://10.0.0.252:8084/api/"   // 长玲电脑
//        hostDebuggable = "https://www.medcn.com/" // yaya 医师授权登录

)
public class NetworkApi {

    @Api("user")
    interface User {

        @Post("bindJPush")
        void bindJPush(String registrationId);

        /**
         * 头像上传
         *
         * @param file
         */
        @Upload("updateAvatar")
        void upload(byte[] file);

        /**
         * 修改个人资料
         *
         * @param userName
         * @param info
         * @param nickName
         */
        @Post("updateInfo")
        void modify(@Query(opt = true) String userName,
                    @Query(opt = true) String info,
                    @Query(opt = true) String nickName);

        /**
         * 修改密码
         *
         * @param oldPwd 旧密码
         * @param newPwd 新密码
         */
        @Post("resetPwd")
        void changePwd(String oldPwd, String newPwd);

        /**
         * 绑定邮箱
         *
         * @param email 用户名
         */
        @Post("toBind/")
        void bindEmail(String email, String password);

        /**
         * 绑定手机
         *
         * @param mobile  手机号
         * @param captcha 验证码
         */
        @Post("bindMobile")
        void bindPhone(String mobile, String captcha);

        /**
         * 解绑邮箱或手机
         *
         * @param type 6代表手机, 7代表邮箱
         */
        @Post("unbind")
        void unBind(@BindId int type);

        /**
         * 绑定或解绑第三方账号
         *
         * @param uniqueId     需要绑定的第三方账号唯一标识,,解绑时无此参数
         * @param thirdPartyId {@link BindId}
         * @param nickName     第三方账号的昵称,解绑时无此参数
         * @param gender       性别,解绑时无此参数
         * @param avatar       头像,解绑时无此参数
         */
        @Post("changeBindStatus")
        void bindAccountStatus(@Query(opt = true) String uniqueId,
                               @Query(opt = true) @BindId int thirdPartyId,
                               @Query(opt = true) String nickName,
                               @Query(opt = true) String gender,
                               @Query(opt = true) String avatar);

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
        @Post("login")
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

        @Post
        @Url(value = "https://www.medcn.com/oauth/app/authorize")
        void yayaLogin(String username, String password);

        /**
         * 获取验证码
         *
         * @param mobile 手机号码
         * @param type   验证码模板类型 0=登录 1=绑定
         */
        @Post("sendCaptcha")
        void sendCaptcha(String mobile, String type);

        /**
         * 邮箱注册
         *
         * @param email    邮箱
         * @param password 密码
         * @param nickName 昵称
         */
        @Post("register")
        void register(String email, String password, String nickName);

        /**
         * 退出登录
         */
        @Post("logout")
        @Retry(count = 5, delay = 1000)
        void logout();

        @Post("login/video")
        void loginVideo(int version);

        @DownloadFile
        @Url
        void downLoad();
    }

    @Api("delivery")
    interface Delivery {
        /**
         * 投稿历史
         *
         * @param pageNum
         * @param pageSize
         */
        @Get("paginate")
        void history(@Query(opt = true) int pageNum,
                     @Query(opt = true) int pageSize);

        /**
         * 投稿历史 指定单位号
         *
         * @param acceptId 接收者id
         */
        @Get("user/detail")
        void historyDetail(int acceptId);

        /**
         * 投稿
         *
         * @param acceptIds 要投稿的单位号ID数组
         * @param courseId  被投稿的课件ID
         */
        @Post("push")
        void unitNum(String acceptIds, String courseId);

        /**
         * 可投稿的单位号
         */
        @Get("acceptors")
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
        void sync(@Query(opt = true) String courseId,
                  @Query(opt = true) int pageNum,
                  @Query(opt = true) String audioUrl);

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
        void uploadAudio(@Query(opt = true) String courseId,
                         @Query(opt = true) String detailId,
                         @Query(opt = true) int playType,
                         @Query(opt = true) int pageNum,
                         @Query(opt = true) byte[] file);

        /**
         * 下载音频
         */
        @DownloadFile
        @Url
        void downloadAudio();

        /**
         * 退出录播/直播页面
         *
         * @param courseId
         * @param pageNum
         * @param over
         */
        @Get("record/exit")
        void exitRecord(String courseId, int pageNum, int over);

        /**
         * 会议列表
         *
         * @param pageNum
         * @param pageSize
         */
        @Post("list")
        void meetingList(@Query(opt = true) int pageNum,
                         @Query(opt = true) int pageSize);

        /**
         * 直播PPT中包含视频 视频页翻页时调用
         *
         * @param courseId
         * @param detailId
         */
        @Get("video/next")
        void videoNext(String courseId, String detailId);

        /**
         * 扫描二维码进入会议
         *
         * @param courseId
         */
        @Get("scan/callback")
        void scan(String courseId);

        @Post("delete")
        void delete(String id);

        @Post("share/copy")
        void copy(String courseId, String title);

        /***
         * 进入会议检测
         *
         * @param courseId
         * @param liveType 0表示课件讲解 1表示视频直播
         */
        @Get("join/check")
        void joinCheck(String courseId, int liveType);
    }

    @Api("charge")
    interface Pay {

        /**
         * Ping++支付
         *
         * @param flux    流量值
         * @param channel 支付方式,按照ping++文档channel属性值给
         */
        @Post("toCharge")
        void pingPay(int flux, String channel);

        /**
         * paypal支付
         *
         * @param flux 流量值
         */
        @Post("createOrder")
        void paypalPay(int flux);

        /**
         * paypal支付结果确认
         *
         * @param paymentId 流量值
         * @param orderId   订单id
         */
        @Post("paypalCallback")
        void paypalPayResult(String paymentId, String orderId);
    }

    @Api
    interface Common {
        /**
         * 忘记密码
         *
         * @param email 邮箱
         */
        @Post("email/findPwd")
        void findPwd(String email);

        /**
         * 扫描二维码
         */
        @Get
        @Url
        void scanQrCode();
    }
}
