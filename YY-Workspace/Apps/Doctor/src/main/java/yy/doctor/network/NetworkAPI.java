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
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.ui.activity.meeting.MeetingFolderActivity.ZeroShowType;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@APIFactory(
        host = "https://app.medyaya.cn/api/",
        hostDebuggable = "http://59.111.90.245:8083/v7/api/"
//        hostDebuggable = "http://10.0.0.234:80/api/" // 礼平电脑
//        hostDebuggable = "http://10.0.0.250:8081/api/" // 轩哥电脑
//        hostDebuggable = "http://10.0.0.252:8082/api/" // 长玲电脑
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
        void login(String username, String password, String openid, String masterId);

        /**
         * 绑定极光推送的ID
         *
         * @param registionId
         */
        @GET("bindJpush")
        @Retry(count = 5, delay = 1000)
        void bindJPush(String registionId);

        /**
         * 登出
         *
         * @param token
         */
        @POST("logout")
        @Retry(count = 5, delay = 1000)
        void logout(String token);

        /**
         * 个人信息
         */
        @GET("user/info")
        void profile();

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

        /**
         * 检查是否已被绑定
         *
         * @param code 微信授权的code
         */
        @POST("check_wx_bind")
        void checkWxBind(String code);

        /**
         * 绑定(解绑)微信
         *
         * @param code 微信授权的code
         */
        @GET("user/set_wx_bind_status")
        void bindWX(@Part(opt = true) String code);

        /**
         * 绑定手机号
         *
         * @param mobile  手机号
         * @param captcha 验证码
         */
        @GET("user/set_bind_mobile")
        void bindMobile(String mobile, String captcha);

        /**
         * 绑定邮箱
         *
         * @param username 用户名
         */
        @GET("email/send_bind_email")
        void bindEmail(String username);

        /**
         * 解绑邮箱
         */
        @GET("user/unbind_email")
        void unBindEmail();

        /**
         * 修改密码
         *
         * @param oldPwd 旧密码
         * @param newPwd 新密码
         */
        @POST("user/resetPwd")
        void changePwd(String oldPwd, String newPwd);

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
         * 收藏的药品目录详情
         *
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

        /**
         * 汤森路透
         *
         * @param preId 不传值的时候，返回汤森路透下一层的子栏目，传值的时候返回该preId下面的子栏目
         */
        @GET("thomson/category")
        void thomson(String preId);
    }

    @API("publicAccount")
    interface UnitNum {
        /**
         * 推荐的单位号
         *
         * @return
         */
        @GET("recommend")
        void recommendUnitNum();

        /**
         * 搜索单位号
         *
         * @param keyword 关键字
         */
        @POST("search")
        void searchUnitNum(String keyword, int pageNum, int pageSize);


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

        /**
         * 象数明细
         */
        @GET("tradeInfo")
        void epnDetails();
    }

    @API("register")
    interface Register {
        /**
         * @param nickname      用户昵称
         * @param linkman       真实姓名
         * @param mobile        手机号
         * @param captcha       验证码
         * @param password      密码
         * @param province      省份
         * @param city          城市
         * @param zone          区县
         * @param hospital      医院名称
         * @param hospitalLevel 医院等级
         * @param category      专科一级名称
         * @param name          专科二级名称
         * @param department    科室名称
         * @param title         职称
         * @param invite        邀请码
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
         *
         * @param preId
         */
        @GET("cities")
        void city(String preId);

        /**
         * 获取验证码
         *
         * @param mobile
         * @param type
         */
        @GET("get_captcha")
        void captcha(String mobile, int type);

        /**
         * 扫一扫
         *
         * @param masterId
         */
        @GET("scan_register")
        void scan(@Part(opt = true) String masterId);

        /**
         * 专科
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
         *
         * @param version
         */
        @POST("properties")
        void config(int version);
    }

    @API
    interface Collection {

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

    @API("meet/")
    interface Meet {
        /**
         * 首页推荐会议(含文件夹)
         */
        @GET("recommend/meet/folder")
        void recommendMeeting(int pageNum, int pageSize);

        /**
         * @param meetId     会议id
         * @param moduleId   模块id
         */

        /**
         * 搜索会议
         *
         * @param keyword 关键字
         */
        @POST("search")
        void searchMeet(String keyword, int pageNum, int pageSize);

        /**
         * 会议列表(关注过的公众的所有会议)
         *
         * @param state  {@link MeetState} 会议状态
         * @param depart 科室类型
         */
        @POST("meets")
        void meets(@Part(opt = true) int state,
                   @Part(opt = true) String depart,
                   @Part(opt = true) int pageNum,
                   @Part(opt = true) int pageSize);

        /**
         * 文件夹
         *
         * @param preId
         * @param showFlag {@link ZeroShowType} 是否显示会议数是0的文件夹
         */
        @GET("folder/leaf")
        void meetFolder(String preId, int showFlag);

        /**
         * 会议科室列表
         */
        @POST("department")
        void meetDepartment();

        /**
         * 会议详情
         */
        @GET("info")
        void meetInfo(String meetId);

        /**
         * 会议资料
         */
        @GET("pageMaterial")
        void meetData(String meetId, int pageNum, int pageSize);

        /**
         * 考试入口
         */
        @GET("toexam")
        void toExam(String meetId, String moduleId);

        /**
         * 问卷入口
         */
        @GET("tosurvey")
        void toSurvey(String meetId, String moduleId);

        /**
         * 视频入口
         */
        @GET("tovideo")
        void toVideo(String meetId, String moduleId);

        /**
         * 签到入口
         */
        @GET("tosign")
        void toSign(String meetId, String moduleId);

        /**
         * 微课(语音+PPT)入口
         */
        @GET("toppt")
        void toCourse(String meetId, String moduleId);

        /**
         * 视频子目录
         */
        @GET("video/sublist")
        void video(String preId);

        /**
         * 提交考试答案
         *
         * @param itemJson 题号+选项的json串
         */
        @POST("submitex")
        void submitExam(@Part(opt = true) String meetId,
                        @Part(opt = true) String moduleId,
                        @Part(opt = true) String paperId,
                        @Part(opt = true) String itemJson);

        /**
         * 提交问卷答案
         *
         * @param itemJson 题号+选项的json串
         */
        @POST("submitsur")
        void submitSurvey(@Part(opt = true) String meetId,
                          @Part(opt = true) String moduleId,
                          @Part(opt = true) String paperId,
                          @Part(opt = true) String itemJson);

        /**
         * 微课学习时间提交
         *
         * @param courseId 微课ID
         * @param details  学习用时
         */
        @POST("ppt/record")
        @Retry(count = 5, delay = 1000)
        void submitCourse(@Part(opt = true) String meetId,
                          @Part(opt = true) String moduleId,
                          @Part(opt = true) String courseId,
                          @Part(opt = true) String details);

        /**
         * @param meetId
         * @param moduleId
         * @param courseId 微课ID
         * @param detailId 视频明细ID
         * @param finished 是否完成
         * @param usedTime 视频用时
         */
        @POST("video/record")
        @Retry(count = 5, delay = 1000)
        void submitVideo(@Part(opt = true) String meetId,
                         @Part(opt = true) String moduleId,
                         @Part(opt = true) String courseId,
                         @Part(opt = true) String detailId,
                         @Part(opt = true) boolean finished,
                         @Part(opt = true, value = "usedtime") String usedTime);

        /**
         * 提交会议学习时间
         *
         * @param useTime 会议学习时间
         */
        @POST("exit")
        @Retry(count = 5, delay = 1000)
        void submitMeet(String meetId, @Part(value = "usedtime") long useTime);

        /**
         * 会议留言记录
         */
        @GET("message/histories")
        void commentHistories(String meetId, int pageSize, int pageNum);

        /**
         * 签到
         *
         * @param positionId 签到位置ID
         * @param signLng    签到经度
         * @param signLat    签到维度
         */
        @POST("sign")
        void sign(@Part(opt = true) String meetId,
                  @Part(opt = true) String moduleId,
                  @Part(opt = true) String positionId,
                  @Part(opt = true) String signLng,
                  @Part(opt = true) String signLat);

        /**
         * 参会统计(个人参会统计)
         *
         * @param offset 偏移量
         */
        @POST("attend_stats")
        void statsMeet(int offset);

        /**
         * 参会统计(关注单位号发布会议统计)
         *
         * @param offset 偏移量
         */
        @POST("publish_stats")
        void statsUnitNum(int offset);
    }

    @API
    interface Common {
        /**
         * 首页banner
         */
        @GET("banner")
        void banner();

        /**
         * 检查app版本
         */
        @GET("version/newly")
        void checkAppVersion();

        /**
         * 象数充值
         *
         * @param subject     商品名称
         * @param totalAmount 商品价格
         */
        @POST("alipay/recharge")
        void epnRecharge(String subject, int totalAmount);

        @DOWNLOAD_FILE
        void download(@Url String url);
    }
}
