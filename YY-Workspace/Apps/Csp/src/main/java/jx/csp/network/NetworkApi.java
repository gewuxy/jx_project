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
        host = "https://www.cspmeeting.com/api/",
        hostDebuggable = "https://www.medcn.cn/csp/api/"
//        hostDebuggable = "https://www.cspmeeting.com/lixuan/api/"
//        hostDebuggable = "http://medcn.synology.me:8889/liping/api/"   // 礼平电脑
//        hostDebuggable = "http://10.0.0.250:8081/api/"   // 轩哥电脑
//        hostDebuggable = "http://10.0.0.252:8083/api/"   // 长玲电脑
//        hostDebuggable = "http://10.0.0.200:8082/api/"   //简亮电脑
//        hostDebuggable = "https://www.medcn.com/" // yaya 医师授权登录
)
public class NetworkApi {

    @Api("user")
    interface User {

        @Post("bindJPush")
        void bindJPush(String registrationId);

        @Get("info")
        void uploadProfileInfo();

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
         * @param info
         * @param nickName
         */
        @Post("updateInfo")
        void modify(@Query(opt = true) String info,
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
        void historyDetail(int acceptId,
                           @Query(opt = true) int pageNum,
                           @Query(opt = true) int pageSize);

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

        /**
         * 投稿平台
         */
        @Get("platform")
        void platform();

        /**
         * 投稿热门单位号和投稿历史
         */
        @Get("hot/unit")
        void contributeHotUnitNum();

        /**
         * 立即投稿
         * @param acceptId  要投稿的单位号ID
         * @param sourceId  投稿的课件ID
         * @param platformId  平台id
         * @param remuneration  稿费
         * @param contact  	联系方式
         */
        @Post("push/unit")
        void immediatelyContribute(int acceptId,
                                   int sourceId,
                                   int platformId,
                                   @Query(opt = true) int remuneration,
                                   @Query(opt = true) String contact);

        /**
         * 搜索单位号
         * @param keyWord
         * @param pageNum
         * @param pageSize
         */
        @Post("search/unit")
        void searchUnitNum(String keyWord, int pageNum, int pageSize);
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
         * 直播语音点击开始直播时调用
         *
         * @param courseId
         * @param imgUrl
         * @param firstClk 是否是第一次点击开始按钮 0表示不是 1表示是
         * @param pageNum  当前下标
         */
        @Get("live/start")
        void start(String courseId, String videoUrl, String imgUrl, int firstClk, int pageNum);

        /**
         * 开始视频直播 未开始的会议第一次点击调用
         *
         * @param courseId
         */
        @Get("live/video/start")
        @Retry(count = 5, delay = 1000)
        void liveVideoStart(String courseId);

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
        @Retry(count = 5, delay = 1000)
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
         * 删除音频
         */
        @Get("delete/audio")
        @Retry(count = 5, delay = 1000)
        void deleteAudio(String detailId);

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
        void meetingList(int pageNum, int pageSize);

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

        /**
         * 删除会议
         *
         * @param id
         */
        @Post("delete")
        void delete(String id);

        @Post("share/copy")
        void copy(String courseId, String title);

        /**
         * 会议设置观看密码
         *
         * @param id
         * @param type
         * @param password
         */
        @Post("set/password")
        void setPassword(String id, String type, String password);

        /**
         * 获取会议密码
         *
         * @param id
         */
        @Get("get/password")
        void getPassword(String id);

        /***
         * 进入会议检测
         *
         * @param courseId
         * @param liveType 0表示课件讲解 1表示视频直播
         */
        @Get("join/check")
        void joinCheck(String courseId, int liveType);

        /**
         * 結束直播
         *
         * @param courseId
         */
        @Get("live/over")
        @Retry(count = 5, delay = 1000)
        void overLive(String courseId);

        /**
         * 会议星评状态
         */
        @Get("star/code")
        void code(String courseId);

        /**
         * 进入编辑讲本
         */
        @Get("edit")
        void editMeet(@Query(opt = true) String courseId);

        /**
         * 获取主题和背景音乐
         *
         * @param type     不传或者传0时，获取主题，传1获取背景音乐
         * @param showType 不传或者传0时，获取推荐的列表，传1获取更多的列表
         * @param courseId 可以不传
         */
        @Get("mini/image/music")
        void editor(@Query(opt = true) int type,
                    @Query(opt = true) int showType,
                    @Query(opt = true) String courseId);

        @Upload("upload/picture")
        void picture(@Query(opt = true) String courseId,
                     byte[] file, int sort);

        /**
         * 完善或修改课件标题，主题，背景音乐
         *
         * @param courseId 课件id
         * @param title    课件标题
         * @param imgId    课件主题id,修改课件时如果没有修改主题，可以不传
         * @param musicId  课件背景音乐id,修改课件时如果没有修改背景音乐，可以不传
         */
        @Post("mini/update")
        @Retry(count = 5, delay = 1000)
        void update(String courseId,
                    @Query(opt = true) String title,
                    @Query(opt = true) int imgId,
                    @Query(opt = true) int musicId);

        /**
         * 创建讲本
         *
         * @param title
         * @param imgId
         * @param musicId
         */
        @Post("/mini/update")
        void create(String courseId,
                    @Query(opt = true) String title,
                    @Query(opt = true) int imgId,
                    @Query(opt = true) int musicId);

        /**
         * 选择背景音乐
         *
         * @param courseId
         * @param musicId  0代表删除 其他代表背景音乐id
         */
        @Post("update/music")
        @Retry(count = 5, delay = 1000)
        void selectBgMusic(String courseId, String musicId);

        /**
         * 选择主题
         *
         * @param courseId
         * @param imgId    值为0表示删除
         */
        @Post("update/img")
        void selectTheme(String courseId, int imgId);

        /**
         * 获取新手指引会议
         */
        @Get("tourist/view")
        void greenHandsGuide(String courseId);

        /**
         * 获取全部背景音乐
         */
        @Get("theme/music/more")
        void bgMusic();

        /**
         * 获取全部的主题
         */
        @Get("theme/image/more")
        void theme();
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

        /**
         * 检查是否有新版本app
         */
        @Get("version/newly")
        void checkAppVersion();

        /**
         * 下载apk
         */
        @DownloadFile
        @Url
        void downloadApk();

        /**
         * 活动列表  目前是进入新手指导
         */
        @Get("activity/list")
        void action();
    }

    @Api
    interface Vip {

        @Post("member/package")
        void vip();

        @Post("member/expire/remind")
        void remind();
    }

    @Api("wallet/")
    interface Wallet {

        @Get("cash")
        void wallet(int pageNum, int pageSize);

        @Get("cash/list")
        void extractSelect(int pageNum, int pageSize);
    }
}
