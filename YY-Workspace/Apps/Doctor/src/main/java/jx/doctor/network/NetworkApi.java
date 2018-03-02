package jx.doctor.network;

import inject.annotation.network.Api;
import inject.annotation.network.Descriptor;
import inject.annotation.network.Query;
import inject.annotation.network.Retry;
import inject.annotation.network.Url;
import inject.annotation.network.method.DownloadFile;
import inject.annotation.network.method.Get;
import inject.annotation.network.method.Post;
import inject.annotation.network.method.Upload;
import jx.doctor.model.meet.Meeting.MeetState;
import jx.doctor.ui.activity.meeting.MeetingFolderActivity.ZeroShowType;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@Descriptor(
        host = "https://app.medyaya.cn/api/",
        hostDebuggable = "https://app.medyaya.cn/api/"
//        hostDebuggable = "http://10.0.0.234:80/api/" // 礼平电脑
//        hostDebuggable = "http://10.0.0.250:8083/api/" // 轩哥电脑
//        hostDebuggable = "http://10.0.0.252:8082/api/" // 长玲电脑
)
public class NetworkApi {

    @Api
    interface User {
        /**
         * 登录(绑定微信号)
         *
         * @param username
         * @param password
         * @param openid
         */
        @Post("login")
        void login(String username, String password, String openid, String masterId);

        /**
         * 绑定极光推送的ID
         *
         * @param registionId
         */
        @Get("bindJpush")
        @Retry(count = 5, delay = 1000)
        void bindJPush(String registionId);

        /**
         * 登出
         *
         * @param token
         */
        @Post("logout")
        @Retry(count = 5, delay = 1000)
        void logout(String token);

        /**
         * 个人信息
         */
        @Get("user/info")
        void profile();

        /**
         * 头像上传
         *
         * @param file
         */
        @Upload("user/update_avatar")
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
        @Post("user/modify")
        void modify(@Query(opt = true) String headimg,
                    @Query(opt = true) String linkman,
                    @Query(opt = true) String hospital,
                    @Query(opt = true) String hospitalLevel,
                    @Query(opt = true) String title,
                    @Query(opt = true) String category,
                    @Query(opt = true) String name,
                    @Query(opt = true) String province,
                    @Query(opt = true) String city,
                    @Query(opt = true) String zone);

        /**
         * 检查是否已被绑定
         *
         * @param code 微信授权的code
         */
        @Post("check_wx_bind")
        void checkWxBind(String code);

        /**
         * 绑定(解绑)微信
         *
         * @param code 微信授权的code
         */
        @Get("user/set_wx_bind_status")
        void bindWX(@Query(opt = true) String code);

        /**
         * 绑定手机号
         *
         * @param mobile  手机号
         * @param captcha 验证码
         */
        @Get("user/set_bind_mobile")
        void bindMobile(String mobile, String captcha);

        /**
         * 绑定邮箱
         *
         * @param username 用户名
         */
        @Get("email/send_bind_email")
        void bindEmail(String username);

        /**
         * 解绑邮箱
         */
        @Get("user/unbind_email")
        void unBindEmail();

        /**
         * 修改密码
         *
         * @param oldPwd 旧密码
         * @param newPwd 新密码
         */
        @Post("user/resetPwd")
        void changePwd(String oldPwd, String newPwd);

        /**
         * 通过邮箱找回密码
         *
         * @param username
         */
        @Get("email/pwd/send_reset_mail")
        void email(String username);

        /**
         * 通过手机找回密码
         *
         * @param mobile
         * @param captcha
         * @param password
         */
        @Get("register/pwd/reset/by_mobile")
        void phone(String mobile, String captcha, String password);
    }

    @Api("data")
    interface Data {
        @DownloadFile
        @Url
        void download();

        /**
         * 药品目录文件或文件夹列表
         *
         * @param preId    父级id,第一级不用传preId
         * @param type     {@link jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType}
         * @param leaf     下一级是否是文件夹, 下一级为文件返回true,下一级是文件夹返回false.第一级传null或空字符串
         * @param pageNum  当前页数
         * @param pageSize 显示条数
         * @return
         */
        @Get("data_category")
        void units(String preId, int type, boolean leaf, int pageNum, int pageSize);

        /**
         * 收藏的药品目录详情
         *
         * @param dataFileId
         */
        @Get("data_detail")
        void collectionDetail(String dataFileId);

        /**
         * 搜索药品或临床指南
         *
         * @param keyword
         * @param type
         * @param pageNum
         * @param pageSize
         */
        @Post("data_search")
        void search(String keyword, int type, int pageNum, int pageSize);

        /**
         * 汤森路透
         *
         * @param preId 不传值的时候，返回汤森路透下一层的子栏目，传值的时候返回该preId下面的子栏目
         */
        @Get("thomson/category")
        void thomson(String preId);
    }

    @Api("publicAccount")
    interface UnitNum {
        /**
         * 推荐的单位号
         *
         * @return
         */
        @Get("recommend")
        void recommendUnitNum();

        /**
         * 搜索单位号
         *
         * @param keyword 关键字
         */
        @Post("search")
        void searchUnitNum(String keyword, int pageNum, int pageSize);


        /**
         * 关注的单位号
         */
        @Get("mySubscribe")
        void attentionUnitNum();

        /**
         * 单位号详情
         *
         * @param id
         * @param pageNum
         * @param pageSize
         */
        @Get("unitInfo")
        void unitNumDetail(int id, int pageNum, int pageSize);

        /**
         * 单位号资料列表
         *
         * @param id
         * @param pageNum
         * @param pageSize
         */
        @Get("materialList")
        void unitNumData(String id, int pageNum, int pageSize);

        /**
         * 关注单位号 取消关注
         *
         * @param masterId
         * @param status   0:取消关注 1：关注
         */
        @Get("subscribe")
        void attention(int masterId, int status);

    }

    @Api("shop")
    interface Epc {

        /**
         * 象城
         *
         * @param pageNum
         * @param pageSize
         */
        @Get("goods")
        void epc(int pageNum, int pageSize);

        /**
         * 商品详情
         *
         * @param id
         */
        @Get("goodInfo")
        void epcDetail(int id);

        /**
         * 订单
         *
         * @param pageNum
         */
        @Get("order")
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
        @Post("buy")
        void exchange(@Query(opt = true) int goodsId,
                      @Query(opt = true) int price,
                      @Query(opt = true) String receiver,
                      @Query(opt = true) String phone,
                      @Query(opt = true) String province,
                      @Query(opt = true) String address);

        /**
         * 象数明细
         */
        @Get("tradeInfo")
        void epnDetails();
    }

    @Api("register")
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

        @Post("reg")
        void reg(@Query(opt = true) String nickname,
                 @Query(opt = true) String linkman,
                 @Query(opt = true) String mobile,
                 @Query(opt = true) String captcha,
                 @Query(opt = true) String password,
                 @Query(opt = true) String province,
                 @Query(opt = true) String city,
                 @Query(opt = true) String zone,
                 @Query(opt = true) String hospital,
                 @Query(opt = true) Integer hospitalLevel,
                 @Query(opt = true) String category,
                 @Query(opt = true) String name,
                 @Query(opt = true) String department,
                 @Query(opt = true) String title,
                 @Query(opt = true) String invite,
                 @Query(opt = true) String masterId);

        /**
         * 省份
         */
        @Get("provinces")
        void province();

        /**
         * 城市
         *
         * @param preId
         */
        @Get("cities")
        void city(String preId);

        /**
         * 获取验证码
         *
         * @param mobile
         * @param type
         */
        @Get("get_captcha")
        void captcha(String mobile, int type);

        /**
         * 扫一扫
         *
         * @param masterId
         */
        @Get("scan_register")
        void scan(@Query(opt = true) String masterId);

        /**
         * 专科
         */
        @Get("specialty")
        void specialty();

        /**
         * 职称
         */
        @Get("title")
        void title();

        /**
         * 配置信息
         *
         * @param version
         */
        @Post("properties")
        void config(int version);
    }

    @Api
    interface Collection {

        /**
         * 收藏或者取消收藏
         *
         * @param resourceId
         * @param type
         * @return
         */
        @Get("set_favorite_status")
        void collectionStatus(String resourceId, @DataType int type);

        /**
         * 收藏的会议列表
         *
         * @param pageNum
         * @param pageSize
         * @return 该值为空或0时，表示会议类型
         */
        @Get("my_favorite")
        void collection(int pageNum, int pageSize, int type);
    }

    @Api("meet/")
    interface Meet {
        /**
         * 首页推荐会议(含文件夹)
         */
        @Get("recommend/meet/folder")
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
        @Post("search")
        void searchMeet(String keyword, int pageNum, int pageSize);

        /**
         * 会议列表(关注过的公众的所有会议)
         *
         * @param state  {@link MeetState} 会议状态
         * @param depart 科室类型
         */
        @Post("meets")
        void meets(@Query(opt = true) int state,
                   @Query(opt = true) String depart,
                   @Query(opt = true) int pageNum,
                   @Query(opt = true) int pageSize);

        /**
         * 文件夹
         *
         * @param preId
         * @param showFlag {@link ZeroShowType} 是否显示会议数是0的文件夹
         */
        @Get("folder/leaf")
        void meetFolder(String preId, int showFlag);

        /**
         * 会议科室列表
         */
        @Post("department")
        void meetDepartment();

        /**
         * 会议详情
         */
        @Get("info")
        void meetInfo(String meetId);

        /**
         * 会议资料
         */
        @Get("pageMaterial")
        void meetData(String meetId, int pageNum, int pageSize);

        /**
         * 考试入口
         */
        @Get("toexam")
        void toExam(String meetId, String moduleId);

        /**
         * 问卷入口
         */
        @Get("tosurvey")
        void toSurvey(String meetId, String moduleId);

        /**
         * 视频入口
         */
        @Get("tovideo")
        void toVideo(String meetId, String moduleId);

        /**
         * 签到入口
         */
        @Get("tosign")
        void toSign(String meetId, String moduleId);

        /**
         * 微课(语音+PPT)入口
         */
        @Get("toppt")
        void toCourse(String meetId, String moduleId);

        /**
         * 视频子目录
         */
        @Get("video/sublist")
        void video(String preId);

        /**
         * 提交考试答案
         *
         * @param itemJson 题号+选项的json串
         */
        @Post("submitex")
        void submitExam(@Query(opt = true) String meetId,
                        @Query(opt = true) String moduleId,
                        @Query(opt = true) String paperId,
                        @Query(opt = true) String itemJson);

        /**
         * 提交问卷答案
         *
         * @param itemJson 题号+选项的json串
         */
        @Post("submitsur")
        void submitSurvey(@Query(opt = true) String meetId,
                          @Query(opt = true) String moduleId,
                          @Query(opt = true) String paperId,
                          @Query(opt = true) String itemJson);

        /**
         * 微课学习时间提交
         *
         * @param courseId 微课ID
         * @param details  学习用时
         */
        @Post("ppt/record")
        @Retry(count = 5, delay = 1000)
        void submitCourse(@Query(opt = true) String meetId,
                          @Query(opt = true) String moduleId,
                          @Query(opt = true) String courseId,
                          @Query(opt = true) String details);

        /**
         * @param meetId
         * @param moduleId
         * @param courseId 微课ID
         * @param detailId 视频明细ID
         * @param finished 是否完成
         * @param usedTime 视频用时
         */
        @Post("video/record")
        @Retry(count = 5, delay = 1000)
        void submitVideo(@Query(opt = true) String meetId,
                         @Query(opt = true) String moduleId,
                         @Query(opt = true) String courseId,
                         @Query(opt = true) String detailId,
                         @Query(opt = true) boolean finished,
                         @Query(opt = true, value = "usedtime") String usedTime);

        /**
         * 提交会议学习时间
         *
         * @param useTime 会议学习时间
         */
        @Post("exit")
        @Retry(count = 5, delay = 1000)
        void submitMeet(String meetId, @Query(value = "usedtime") long useTime);

        /**
         * 会议留言记录
         */
        @Get("message/histories")
        void commentHistories(String meetId, int pageSize, int pageNum);

        /**
         * 签到
         *
         * @param positionId 签到位置ID
         * @param signLng    签到经度
         * @param signLat    签到维度
         */
        @Post("sign")
        void sign(@Query(opt = true) String meetId,
                  @Query(opt = true) String moduleId,
                  @Query(opt = true) String positionId,
                  @Query(opt = true) String signLng,
                  @Query(opt = true) String signLat);

        /**
         * 参会统计(个人参会统计)
         *
         * @param offset 偏移量
         */
        @Post("attend_stats")
        void statsMeet(int offset);

        /**
         * 参会统计(关注单位号发布会议统计)
         *
         * @param offset 偏移量
         */
        @Post("publish_stats")
        void statsUnitNum(int offset);
    }

    @Api
    interface Common {
        /**
         * 首页banner
         */
        @Get("banner")
        void banner();

        /**
         * 检查app版本
         */
        @Get("version/newly")
        void checkAppVersion();

        /**
         * 象数充值
         *
         * @param subject     商品名称
         * @param totalAmount 商品价格
         */
        @Post("alipay/recharge")
        void epnRecharge(String subject, int totalAmount);

        @DownloadFile
        @Url
        void download();

        /**
         * 启动页广告
         */
        @Get("advert")
        void advert();
    }
}
