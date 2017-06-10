package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.network.model.param.CommonPair;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.UrlUtil.UrlData;
import yy.doctor.network.UrlUtil.UrlEpc;
import yy.doctor.network.UrlUtil.UrlEpn;
import yy.doctor.network.UrlUtil.UrlHome;
import yy.doctor.network.UrlUtil.UrlMeet;
import yy.doctor.network.UrlUtil.UrlRegister;
import yy.doctor.network.UrlUtil.UrlSearch;
import yy.doctor.network.UrlUtil.UrlUnitNum;
import yy.doctor.network.UrlUtil.UrlUser;
import yy.doctor.network.builder.ExchangeBuilder;
import yy.doctor.network.builder.ModifyBuilder;
import yy.doctor.network.builder.RegisterBuilder;
import yy.doctor.network.builder.SignBuilder;
import yy.doctor.network.builder.SubmitBuilder;


/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class NetFactory {

    private static final String TAG = NetFactory.class.getSimpleName();

    private interface CommonParam {
        String KToken = "token";
    }

    public interface RegisterParam {
        String KInvite = "invite";//邀请码
        String KUsername = "username";//用户名
        String KNickname = "nickname";//昵称
        String KLinkman = "linkman";//真实名字
        String KMobile = "mobile";//手机号
        String KPwd = "password";//密码
        String KProvince = "province";//省份
        String KCity = "city";//城市
        String KArea = "zone"; // 区县
        String KHospital = "hospital";//医院
        String KDepartment = "department";//科室
        String KLicence = "licence";//执业许可证号
    }

    public interface UserParam {
        String KUserName = "username";
        String KPassword = "password";
        String KOldPwd = "oldpwd";
        String KNewPwd = "newpwd";
        String KJPushRegisterId = "registionId";
    }

    public interface MeetParam {
        String KState = "state";//会议状态
        String KDepart = "depart";//

        String KMeetId = "meetId";//会议
        String KPaperId = "paperId";//试卷
        String KModuleId = "moduleId";//模块
        String KSurveyId = "surveyId";//问卷
        String KCourseId = "courseId";//微课
        String KPreId = "preId";//视频明细
        String KDetailId = "detailId";//微课明细
        String KQuestionId = "questionId";//试题
        String KPositionId = "positionId";//签到位置

        String KAnswer = "answer";//答案
        String KItemJson = "itemJson";//答案列表

        String KUseTime = "useTime";//微课用时

        String KMessage = "message";//留言内容
        String KMsgType = "msgType";//留言类型
        String KPageNum = "pageNum";//第N次分页
        String KPageSize = "pageSize";//返回多少条数据

        String KSignLng = "signLng";//经度
        String KSignLat = "signLat";//维度
    }

    private interface HomeParam {
        String KType = "type";
    }

    public interface ProfileParam {
        String KHeadImgUrl = "headimg";   //头像地址
        String KLinkman = "linkman";   //真实姓名
        String KHospital = "hospital";   //医院
        String KDepartment = "department";   //科室
        String KHospitalLevel = "hosLevel";  //医院等级
        String KCMEId = "cmeId";  //CME卡号
        String KLicence = "licence";   //执业许可证
        String KTitle = "title";   //职称
        String KProvince = "province";   //省份
        String KCity = "city";    //城市
        String KArea = "zone";  //区

        String KNickname = "nickname";   //用户昵称
        String KMobile = "mobile";    //手机号
        String KMajor = "major";    //专长
        String KPlace = "place";   //职务
        String KAddress = "address";   //地址
        String KGender = "gender";    //性别
        String KDegree = "degree";  //学历
    }

    private interface UpHeadImgParam {
        String KFile = "file";   //文件
    }

    public interface CityParam {
        String KCity = "preid";  //省份ID
    }

    public interface EpnRechargeParam {
        String KBody = "body";  //商品描述
        String KSubject = "subject";  //商品名称
        String KTotalAmount = "totalAmount";  //商品价格
    }

    private interface AttentionParam {
        String KMasterId = "masterId";    // 关注/取消关注的公众号id
        String KTurnTo = "turnTo";     // 0:取消关注 1：关注
    }

    private interface UnitNumDetailParam {
        String KId = "id";  //单位号id
        String KPageNum = "pageNum";  //页数
        String KPageSize = "pageSize";  //条数
    }

    private interface SearchParam {
        String KKeyword = "keyword";  //关键字
    }

    public interface EpcExchangeParam {
        String KGoodsId = "goodsId";    //商品id
        String KPrice = "price";    //商品价格
        String KReceiver = "receiver";    //收货人
        String KPhone = "phone";    //手机号
        String KProvince = "province";   //省份
        String KAddress = "address";    //地址
        String KBuyLimit = "buyLimit";   //商品限购数
    }

    public interface EpcDetailParam {
        String KEpcDetail = "id";  //商品id
    }

    public interface CollectMeetingParam {
        String KMeetingId = "meetId";
        String KTurnTo = "turnTo";
    }

    public interface CollectionMeetingsParam {
        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
        String KType = "type";
    }

    public interface ThomsonParam {
        String KPreId = "preId";
        String KCategoryId = "categoryId";
        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
    }

    /**
     * 开机广告
     *
     * @return
     */
    public static NetworkReq ad() {
        return newGet(UrlUser.KAd)
                .build();
    }

    /**
     * 注册
     */
    public static RegisterBuilder register() {
        return new RegisterBuilder();
    }

    /**
     * 医院信息
     *
     * @param city
     * @return
     */
    public static NetworkReq hospital(String city) {
        return newPost(UrlRegister.KHospital)
                .param(RegisterParam.KCity, city)
                .build();
    }

    /**
     * 科室信息
     *
     * @return
     */
    public static NetworkReq section() {
        return newGet(UrlRegister.KDepart)
                .build();
    }

    /**
     * 登录
     *
     * @param name
     * @param pwd
     * @return
     */
    public static NetworkReq login(String name, String pwd) {
        return newGet(UrlUser.KLogin)
                .param(UserParam.KUserName, name)
                .param(UserParam.KPassword, pwd)
                .build();
    }

    /**
     * 绑定极光推送的ID
     *
     * @param registerId
     * @return
     */
    public static NetworkReq bindJPush(String registerId) {
        return newGet(UrlUser.KBindJPush)
                .param(UserParam.KJPushRegisterId, registerId)
                .build();
    }

    /**
     * 忘记密码
     *
     * @param username
     * @return
     */
    public static NetworkReq forgetPwd(String username) {
        return newGet(UrlUser.KForgetPwd)
                .param(UserParam.KUserName, username)
                .build();
    }

    /**
     * 登出
     *
     * @return
     */
    public static NetworkReq logout() {
        return newGet(UrlUser.KLogout)
                .param(CommonParam.KToken, Profile.inst().getString(TProfile.token))
                .retry(5, 1)
                .build();
    }

    /**
     * 首页banner
     *
     * @return
     */
    public static NetworkReq banner() {
        return newGet(UrlHome.KBanner)
                .build();
    }

    /**
     * 首页推荐会议
     *
     * @return
     */
    public static NetworkReq recommendMeeting() {
        return newGet(UrlHome.KRecommendMeeting)
                .build();
    }

    /**
     * 首页推荐单位号
     *
     * @return
     */
    public static NetworkReq recommendUnitNum() {
        return newGet(UrlHome.KRecommendUnitNum)
                .build();
    }

    /**
     * 个人信息
     *
     * @return
     */
    public static NetworkReq profile() {
        return newGet(UrlUser.KProfile).build();
    }

    /**
     * 个人信息修改
     *
     * @return
     */
    public static ModifyBuilder newModifyBuilder() {
        return new ModifyBuilder();
    }

    /**
     * 头像上传
     *
     * @return
     */
    public static NetworkReq upheadimg(byte[] bytes) {
        return newUpload(UrlUser.KUpHeaderImg)
                .param(UpHeadImgParam.KFile, bytes)
                .build();
    }

    /**
     * 忘记密码
     *
     * @param oldPwd
     * @param newPwd
     * @return
     */
    public static NetworkReq changePwd(String oldPwd, String newPwd) {
        return newPost(UrlUser.KChangePwd)
                .param(UserParam.KOldPwd, oldPwd)
                .param(UserParam.KNewPwd, newPwd)
                .build();
    }

    /**
     * 省份
     *
     * @return
     */
    public static NetworkReq province() {
        return newGet(UrlRegister.KProvince)
                .build();
    }

    /**
     * 城市
     *
     * @return
     */
    public static NetworkReq city(String preId) {
        return newGet(UrlRegister.KCity)
                .param(CityParam.KCity, preId)
                .build();
    }

    /**
     * 收藏的会议列表
     *
     * @param pageNum
     * @param pageSize
     * @param type     该值为空或0时，表示会议类型
     * @return
     */
    public static NetworkReq collectionMeetings(int pageNum, int pageSize, int type) {
        return newGet(UrlUser.KCollectionMeetings)
                .param(CollectionMeetingsParam.KPageNum, pageNum)
                .param(CollectionMeetingsParam.KPageSize, pageSize)
                .param(CollectionMeetingsParam.KType, type)
                .build();
    }

    public static NetworkReq collectionMeetings(int pageNum, int pageSize) {
        return collectionMeetings(pageNum, pageSize, 0);
    }

    /**
     * 象数明细
     *
     * @return
     */
    public static NetworkReq epnDetails() {
        return newGet(UrlEpn.KEpnDetails)
                .build();
    }

    /**
     * 象数充值
     *
     * @return
     */
    public static NetworkReq epnRecharge(String subject, int totalAmount) {
        return newPost(UrlEpn.KEpnRecharge)
                .param(EpnRechargeParam.KSubject, subject)
                .param(EpnRechargeParam.KTotalAmount, totalAmount)
                .build();
    }

    /**
     * 商品兑换
     *
     * @return
     */
    public static ExchangeBuilder newExchangeBuilder() {
        return new ExchangeBuilder();
    }

    /**
     * 订单
     *
     * @return
     */
    public static NetworkReq order() {
        return newGet(UrlEpc.KOrder)
                .build();
    }

    /**
     * 象城
     *
     * @return
     */
    public static NetworkReq epc() {
        return newGet(UrlEpc.KEpc)
                .build();
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    public static NetworkReq epcDetail(int id) {
        return newGet(UrlEpc.KEpcDetail)
                .param(EpcDetailParam.KEpcDetail, id)
                .build();
    }

    /**
     * 关注的单位号
     *
     * @return
     */
    public static NetworkReq unitNum() {
        return newGet(UrlUnitNum.KUnitNum)
                .build();
    }

    /**
     * 关注单位号 取消关注
     *
     * @param masterId
     * @param turnTo   0:取消关注 1：关注
     * @return
     */
    public static NetworkReq attention(int masterId, int turnTo) {
        return newGet(UrlUnitNum.KAttention)
                .param(AttentionParam.KMasterId, masterId)
                .param(AttentionParam.KTurnTo, turnTo)
                .build();
    }

    /**
     * 单位号详情
     *
     * @param id
     * @param page
     * @param size
     * @return
     */
    public static NetworkReq unitNumDetail(int id, int page, int size) {
        return newGet(UrlUnitNum.KUnitNumDetail)
                .param(UnitNumDetailParam.KId, id)
                .param(UnitNumDetailParam.KPageNum, page)
                .param(UnitNumDetailParam.KPageSize, size)
                .build();
    }

    /**
     * 单位号资料列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static NetworkReq unitNumData(int id, int pageNum, int pageSize) {
        return newGet(UrlUnitNum.KUnitNumData)
                .param(UnitNumDetailParam.KId, id)
                .param(UnitNumDetailParam.KPageNum, pageNum)
                .param(UnitNumDetailParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 单位号资料下载
     *
     * @param url
     * @param filePath
     * @param fileName
     * @return
     */
    public static NetworkReq downloadData(String url, String filePath, String fileName) {
        return newDownload(url, filePath, fileName)
                .build();
    }

    /**
     * 汤森路透的所有信息
     *
     * @return
     */
    public static NetworkReq thomsonAll() {
        return newGet(UrlData.KThomsonAll).build();
    }

    /**
     * 汤森路透
     *
     * @param preId 不传值的时候，返回汤森路透下一层的子栏目，传值的时候返回该preId下面的子栏目
     * @return
     */
    public static NetworkReq thomson(String preId) {
        return newGet(UrlData.KThomson)
                .param(ThomsonParam.KPreId, preId)
                .build();
    }

    /**
     * 汤森路透资料
     *
     * @param categoryId 上一功能中的category的id
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static NetworkReq thomsonData(String categoryId, int pageNum, int pageSize) {
        return newGet(UrlData.KThomSonData)
                .param(ThomsonParam.KCategoryId, categoryId)
                .param(ThomsonParam.KPageNum, pageNum)
                .param(ThomsonParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 搜索页面推荐的单位号
     *
     * @return
     */
    public static NetworkReq searchRecUnitNum() {
        return newGet(UrlSearch.KSearchRecUnitNum)
                .build();
    }

    /**
     * 搜索单位号
     *
     * @param keyword
     * @return
     */
    public static NetworkReq searchUnitNum(String keyword) {
        return newPost(UrlSearch.KSearchUnitNum)
                .param(SearchParam.KKeyword, keyword)
                .build();
    }

    /**
     * 搜索会议
     *
     * @param keyword
     * @return
     */
    public static NetworkReq searchMeeting(String keyword) {
        return newPost(UrlSearch.KSearchMeeting)
                .param(SearchParam.KKeyword, keyword)
                .build();
    }

    /**
     * 关注过的公众的所有会议
     */
    public static NetworkReq meets(int state, String depart) {
        return newPost(UrlMeet.KMeets)
                .param(MeetParam.KState, state)
                .param(MeetParam.KDepart, depart)
                .build();
    }

    /**
     * 返回会议搜索时的科室列表选择
     * TODO:调用时机?
     */
    public static NetworkReq types() {
        return newGet(UrlMeet.KTypes)
                .build();
    }

    /**
     * 会议详情
     */
    public static NetworkReq meetInfo(String meetId) {
        return newGet(UrlMeet.KInfo)
                .param(MeetParam.KMeetId, meetId)
                .build();
    }

    public static NetworkReq toBase(String url, String meetId, String moduleId) {
        return newGet(url)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KModuleId, moduleId)
                .build();
    }

    /**
     * 考试入口
     */
    public static NetworkReq toExam(String meetId, String moduleId) {
        return toBase(UrlMeet.KToExam, meetId, moduleId);
    }

    /**
     * 问卷入口
     */
    public static NetworkReq toSurvey(String meetId, String moduleId) {
        return toBase(UrlMeet.KToSurvey, meetId, moduleId);
    }

    /**
     * 微课(语音+PPT)入口
     */
    public static NetworkReq toPPT(String meetId, String moduleId) {
        return toBase(UrlMeet.KToPPT, meetId, moduleId);
    }

    /**
     * 签到入口
     */
    public static NetworkReq toSign(String meetId, String moduleId) {
        return toBase(UrlMeet.KToSign, meetId, moduleId);
    }

    /**
     * 视频入口
     */
    public static NetworkReq toVideo(String meetId, String moduleId) {
        return toBase(UrlMeet.KToVideo, meetId, moduleId);
    }

    /**
     * 考试提交
     */
    public static SubmitBuilder submitEx() {
        return new SubmitBuilder(UrlMeet.KSubmitEx);
    }
    /**
     * 问卷提交
     */
    public static SubmitBuilder submitSur() {
        return new SubmitBuilder(UrlMeet.KSubmitSur);
    }

    /**
     * 微课学习提交
     */
    public static SubmitBuilder submitPpt() {
        return new SubmitBuilder(UrlMeet.KToPPT);
    }

    /**
     * 签到
     */
    public static SignBuilder sign() {
        return new SignBuilder();
    }

    /**
     * 会议留言记录
     */
    public static NetworkReq histories(String meetId) {
        return newGet(UrlMeet.KHistories)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KPageSize, 10)
                .param(MeetParam.KPageNum, 1)
                .build();
    }

    /**
     * 查询视频子目录
     */
    public static NetworkReq video(String preId) {
        return newGet(UrlMeet.KVideo)
                .param(MeetParam.KPreId, preId)
                .build();
    }

    /**
     * 会议留言记录
     */
    public static NetworkReq histories(String meetId, String pageSize, String pageNum) {
        return newGet(UrlMeet.KHistories)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KPageSize, pageSize)
                .param(MeetParam.KPageNum, pageNum)
                .build();
    }

    /**
     * 发表会议留言
     */
    public static NetworkReq send(String meetId, String message, String msgType) {
        return newPost(UrlMeet.KSend)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KMessage, message)
                .param(MeetParam.KMsgType, msgType)
                .build();
    }

    /**
     * 收藏会议
     *
     * @param meetId
     * @param turnTo 0:取消收藏，1:收藏
     * @return
     */
    public static NetworkReq collectMeeting(String meetId, int turnTo) {
        return newGet(UrlMeet.KCollectMeeting)
                .param(CollectMeetingParam.KMeetingId, meetId)
                .param(CollectMeetingParam.KTurnTo, turnTo)
                .build();
    }

    /*********************************
     * 以下是工具
     */

    /**
     * 获取post请求
     *
     * @param url
     * @return
     */
    public static Builder newPost(String url) {
        return NetworkReq.newBuilder(UrlUtil.getBaseUrl() + url)
                .post()
                .header(getBaseHeader());
    }

    /**
     * 获取get请求
     *
     * @param url
     * @return
     */
    public static Builder newGet(String url) {
        return NetworkReq.newBuilder(UrlUtil.getBaseUrl() + url)
                .get()
                .header(getBaseHeader());
    }

    /**
     * 获取upload请求
     *
     * @param url
     * @return
     */
    public static Builder newUpload(String url) {
        return NetworkReq.newBuilder(UrlUtil.getBaseUrl() + url)
                .upload()
                .header(getBaseHeader());
    }

    /**
     * 获取download请求
     *
     * @param url
     * @param filePath
     * @param fileName
     * @return
     */
    public static Builder newDownload(String url, String filePath, String fileName) {
        return NetworkReq.newBuilder(url)
                .downloadFile(filePath, fileName)
                .header(getBaseHeader());
    }

    private static List<CommonPair> getBaseHeader() {
        List<CommonPair> ps = new ArrayList<>();

        // TODO: ???公共参数
        // ps.add(newPair(BaseParam.device_os, "android"));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.KToken, Profile.inst().getString(TProfile.token)));
        }
        return ps;
    }

    private static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }

}
