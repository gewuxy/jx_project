package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import lib.network.param.NameValuePair;
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

    public interface HospitalParam {
        String city = "city";
    }

    public interface UserParam {
        String username = "username";
        String password = "password";
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

    private interface HomePara {
        String type = "type";
    }

    public interface ProfilePara {
        String nickname = "nickname";
        String linkman = "linkman";
        String mobile = "mobile";
        String headimg = "headimg";
        String province = "province";
        String city = "city";
        String licence = "licence";
        String major = "major";
        String place = "place";
        String title = "title";
        String hospital = "hospital";
        String department = "department";
        String address = "address";
    }

    private interface UpHeadImgPara {
        String file = "file";
    }

    public interface CityPara {
        String city = "preid";
    }

    public interface EpnRechargePara {
        String body = "body";
        String subject = "subject";
        String totalAmount = "totalAmount";
    }

    private interface AttentionPara {
        String masterId = "masterId";
        String turnTo = "turnTo";
    }

    private interface UnitNumDetailPara {
        String id = "id";
    }

    private interface SearchPara {
        String keyword = "keyword";
    }

    public interface EpcExchangePara {
        String goodsId = "goodsId";
        String price = "price";
        String receiver = "receiver";
        String phone = "phone";
        String province = "province";
        String address = "address";
        String buyLimit = "buyLimit";
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
    public static NetworkRequest hospital(String city) {
        return newGet(UrlRegister.KHospital)
                .param(HospitalParam.city, city)
                .build();
    }

    /**
     * 科室信息
     *
     * @return
     */
    public static NetworkRequest depart() {
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
    public static NetworkRequest login(String name, String pwd) {
        return newGet(UrlUser.login)
                .param(UserParam.username, name)
                .param(UserParam.password, pwd)
                .build();
    }

    /**
     * 登出
     *
     * @return
     */
    public static NetworkRequest logout() {
        return newGet(UrlUser.logout)
                .param(CommonParam.KToken, Profile.inst().getString(token))
                .build();
    }

    /**
     * 个人信息
     *
     * @return
     */
    public static NetworkRequest profile() {
        return newGet(UrlUser.profile).build();
    }

    /**
     * 首页的banner
     *
     * @param type
     * @return
     */
    public static NetworkRequest banner(int type) {
        return newGet(UrlHome.banner)
                .param(HomePara.type, type)
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
    public static NetworkRequest upheadimg(byte[] bytes) {
        return newUpload(UrlUser.upheadimg)
                .param(UpHeadImgPara.file, bytes)
                .build();
    }

    /**
     * 省份
     *
     * @return
     */
    public static NetworkRequest province() {
        return newGet(UrlRegister.KProvince)
                .build();
    }

    /**
     * 城市
     *
     * @return
     */
    public static NetworkRequest city(String preid) {
        return newGet(UrlRegister.KCity)
                .param(CityPara.city, preid)
                .build();
    }

    /**
     * 象数明细
     *
     * @return
     */
    public static NetworkRequest epnDedails() {
        return newGet(UrlEpn.epndetails)
                .build();
    }

    /**
     * 象数充值
     *
     * @return
     */
    public static NetworkRequest epnRecharge(String subject, String totalAmount) {
        return newPost(UrlEpn.epnrecharge)
                .param(EpnRechargePara.subject, subject)
                .param(EpnRechargePara.totalAmount, totalAmount)
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
    public static NetworkRequest order() {
        return newGet(UrlEpc.order)
                .build();
    }

    /**
     * 象城
     *
     * @return
     */
    public static NetworkRequest epc() {
        return newGet(UrlEpc.epc)
                .build();
    }

    /**
     * 关注的单位号
     *
     * @return
     */
    public static NetworkRequest unitNum() {
        return newGet(UrlUnitNum.unitNum)
                .build();
    }

    /**
     * 关注单位号 取消关注
     *
     * @param masterId
     * @param turnTo   0:取消关注 1：关注
     * @return
     */
    public static NetworkRequest attention(int masterId, int turnTo) {
        return newGet(UrlUnitNum.attention)
                .param(AttentionPara.masterId, masterId)
                .param(AttentionPara.turnTo, turnTo)
                .build();
    }

    /**
     * 单位号详情
     *
     * @param id
     * @return
     */
    public static NetworkRequest unitNumDetail(int id) {
        return newGet(UrlUnitNum.unitNumDetail)
                .param(UnitNumDetailPara.id, id)
                .build();
    }

    /**
     * 搜索页面的推荐单位号
     *
     * @return
     */
    public static NetworkRequest recommendUnitNum() {
        return newGet(UrlSearch.recommendUnitNum)
                .build();
    }

    /**
     * 搜索单位号
     *
     * @param keyword
     * @return
     */
    public static NetworkRequest searchUnitnum(String keyword) {
        return newPost(UrlSearch.searchUnitNum)
                .param(SearchPara.keyword, keyword)
                .build();
    }

    /**
     * 会议推荐
     */
    public static NetworkRequest meetRec() {
        return newGet(UrlMeet.KMeetRec)
                .build();
    }

    /**
     * 关注过的公众的所有会议
     */
    public static NetworkRequest meets(int state) {
        return newGet(UrlMeet.KMeets)
                .param(MeetParam.KState, state)
                .build();
    }

    /**
     * 会议详情
     */
    public static NetworkRequest meetInfo(String meetId) {
        return newGet(UrlMeet.KInfo)
                .param(MeetParam.KMeetId, meetId)
                .build();
    }


    public static NetworkRequest toBase(String url, String meetId, String moduleId) {
        return newGet(url)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KModuleId, moduleId)
                .build();
    }

    /**
     * 考试入口
     */
    public static NetworkRequest toExam(String meetId, String moduleId) {
        return toBase(UrlMeet.KToExam, meetId, moduleId);
    }

    /**
     * 问卷入口
     */
    public static NetworkRequest toSurvey(String meetId, String moduleId) {
        return toBase(UrlMeet.KToSurvey, meetId, moduleId);
    }

    /**
     * 微课(语音+PPT)入口
     */
    public static NetworkRequest toPpt(String meetId, String moduleId) {
        return toBase(UrlMeet.KToPpt, meetId, moduleId);
    }

    /**
     * 签到入口
     */
    public static NetworkRequest toSign(String meetId, String moduleId) {
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
    public static NetworkRequest histories(String meetId) {
        return newGet(UrlMeet.KHistories)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KPageSize, 10)
                .param(MeetParam.KPageNum, 1)
                .build();
    }

    /**
     * 会议留言记录
     */
    public static NetworkRequest histories(String meetId, String pageSize, String pageNum) {
        return newGet(UrlMeet.KHistories)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KPageSize, pageSize)
                .param(MeetParam.KPageNum, pageNum)
                .build();
    }

    /**
     * 发表会议留言
     */
    public static NetworkRequest send(String meetId, String message, String msgType) {
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

    private static List<NameValuePair> getBaseHeader() {
        List<NameValuePair> ps = new ArrayList<>();

        // TODO: ???公共参数
//        ps.add(newPair(BaseParam.device_os, "android"));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.KToken, Profile.inst().getString(token)));
        }

        return ps;
    }

    private static NameValuePair newPair(String key, Object value) {
        return new NameValuePair(key, value);
    }

}
