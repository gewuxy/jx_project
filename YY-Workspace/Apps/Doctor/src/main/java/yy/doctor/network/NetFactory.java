package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.network.param.CommonPair;
import yy.doctor.model.Profile;
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

import static yy.doctor.model.Profile.TProfile.token;

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
        String KProvince = "provice";//省份
        String KCity = "city";//城市
        String KHospital = "hospital";//医院
        String KDepartment = "department";//科室
        String KLicence = "licence";//执业许可证号
    }

    public interface UserParam {
        String KUserName = "username";
        String KPassword = "password";
    }

    public interface MeetParam {
        String KState = "state";//会议状态

        String KMeetId = "meetId";//会议
        String KPaperId = "paperId";//试卷
        String KModuleId = "moduleId";//模块
        String KSurveyId = "surveyId";//问卷
        String KCourseId = "courseId";//微课
        String KDetailId = "detailId";//微课明细
        String KQuestionId = "questionId";//试题
        String KPositionId = "positionId";//签到位置

        String KAnswer = "answer";//答案
        String KItemJson = "itemJSON";//答案列表

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
        String KNickname = "nickname";   //用户昵称
        String KLinkman = "linkman";   //真实姓名
        String KMobile = "mobile";    //手机号
        String KHeadImgUrl = "headimg";   //头像地址
        String KProvince = "province";   //省份
        String KCity = "city";    //城市
        String KLicence = "licence";   //执业许可证
        String KMajor = "major";    //专长
        String KPlace = "place";   //职务
        String KTitle = "title";   //职位
        String KHospital = "hospital";   //医院
        String KDepartment = "department";   //科室
        String KAddress = "address";   //地址
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
        String KProvince = "KProvince";   //省份
        String KAddress = "KAddress";    //地址
        String KBuyLimit = "buyLimit";   //商品限购数
    }

    public interface EpcDetailParam {
        String KEpcDetail = "id";  //商品id
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
     * 登出
     *
     * @return
     */
    public static NetworkReq logout() {
        return newGet(UrlUser.KLogout)
                .param(CommonParam.KToken, Profile.inst().getString(token))
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
     * 个人信息
     *
     * @return
     */
    public static NetworkReq profile() {
        return newGet(UrlUser.KProfile).build();
    }

    /**
     * 首页的banner
     *
     * @param type
     * @return
     */
    public static NetworkReq banner(int type) {
        return newGet(UrlHome.KBanner)
                .build();
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
     * 象数明细
     *
     * @return
     */
    public static NetworkReq epnDedails() {
        return newGet(UrlEpn.KEpnDetails)
                .build();
    }

    /**
     * 象数充值
     *
     * @return
     */
    public static NetworkReq epnRecharge(String subject, String totalAmount) {
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
     * 搜索页面的推荐单位号
     *
     * @return
     */
    public static NetworkReq recommendUnitNum() {
        return newGet(UrlSearch.KRecommendUnitNum)
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
     * 关注过的公众的所有会议
     */
    public static NetworkReq meets(int state) {
        return newGet(UrlMeet.KMeets)
                .param(MeetParam.KState, state)
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
    public static NetworkReq toPpt(String meetId, String moduleId) {
        return toBase(UrlMeet.KToPpt, meetId, moduleId);
    }

    /**
     * 签到入口
     */
    public static NetworkReq toSign(String meetId, String moduleId) {
        return toBase(UrlMeet.KToSign, meetId, moduleId);
    }

    /**
     * 考试提交
     */
    public static SubmitBuilder submitEx() {
        return new SubmitBuilder(UrlMeet.KSubmitEx);
    }

    /**
     * 微课学习提交
     */
    public static SubmitBuilder submitPpt() {
        return new SubmitBuilder(UrlMeet.KToPpt);
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
        return new Builder(UrlUtil.getBaseUrl() + url)
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
        return new Builder(UrlUtil.getBaseUrl() + url)
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
        return new Builder(UrlUtil.getBaseUrl() + url)
                .upload()
                .header(getBaseHeader());
    }

    private static List<CommonPair> getBaseHeader() {
        List<CommonPair> ps = new ArrayList<>();

        // TODO: ???公共参数
//        ps.add(newPair(BaseParam.device_os, "android"));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.KToken, Profile.inst().getString(token)));
        }

        return ps;
    }

    private static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }

}
