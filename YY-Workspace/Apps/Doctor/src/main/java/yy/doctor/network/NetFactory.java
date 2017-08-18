package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.network.model.param.CommonPair;
import lib.ys.util.DeviceUtil;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.UrlUtil.UrlMeet;
import yy.doctor.network.UrlUtil.UrlUser;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class NetFactory {

    private static final String TAG = NetFactory.class.getSimpleName();


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

    public static Builder newPost(String url, int pageNum, int pageSize) {
        return NetworkReq.newBuilder(UrlUtil.getBaseUrl() + url)
                .post()
                .param(CommonParam.KPageNum, pageNum)
                .param(CommonParam.KPageSize, pageSize)
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

    public static Builder newGet(String url, int pageNum, int pageSize) {
        return NetworkReq.newBuilder(UrlUtil.getBaseUrl() + url)
                .get()
                .param(CommonParam.KPageNum, pageNum)
                .param(CommonParam.KPageSize, pageSize)
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
                .download(filePath, fileName)
                .header(getBaseHeader());
    }

    private static List<CommonPair> getBaseHeader() {
        List<CommonPair> ps = new ArrayList<>();

        ps.add(newPair(BaseParam.KDevice, "android"));
        ps.add(newPair(BaseParam.KOSVersion, DeviceUtil.getSystemVersion()));
        ps.add(newPair(BaseParam.KAppVersion, DeviceUtil.getAppVersion()));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.KToken, Profile.inst().getString(TProfile.token)));
        }
        return ps;
    }

    public static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }

    public interface BaseParam {
        String KOSVersion = "os_version";
        String KDevice = "os_type";
        String KAppVersion = "app_version";
    }

    public interface CommonParam {
        String KToken = "token";
        String KPreId = "preId";
        String KOffset = "offset";

        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
    }

    public interface RegisterParam {
        String KUsername = "username";//用户名
        String KNickname = "nickname";//昵称
        String KLinkman = "linkman";//真实名字
        String KMobile = "mobile";//手机号
        String KType = "type";
        String KPassword = "password";//密码
        String KProvince = "province";//省份
        String KCity = "city";//城市
        String KZone = "zone"; // 区县
        String KHospital = "hospital";//医院
        String KCategory = "category";//专科一级名称
        String KName = "name";//专科一级名称
        String KHospitalLevel = "hospitalLevel";//医院级别
        String KDepartment = "department";//科室
        String KInvite = "invite";//科室
        String KTitle = "title";//邀请码
        String KLicence = "licence";//执业许可证号
        String KCaptcha = "captcha";//注册验证码
        String KMasterId = "masterId";//二维码参数，激活码提供方id
        String KVersion = "version"; // 配置信息的版本号
    }

    public interface UserParam {
        String KUserName = "username";
        String KPassword = "password";
        String KOldPwd = "oldPwd";
        String KNewPwd = "newPwd";
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
        String KDetails = "details";//答案列表

        String KUseTime = "usedtime";//微课用时  秒

        String KMessage = "message";//留言内容
        String KMsgType = "msgType";//留言类型

        String KSignLng = "signLng";//经度
        String KSignLat = "signLat";//维度

        String KFinish = "finished"; //  是否完成
        String KShowFlag = "showFlag"; // 是否显示0
    }

    private interface HomeParam {
        String KType = "type";
    }

    public interface ProfileParam {
        String KHeadImgUrl = "headimg";   //头像地址
        String KLinkman = "linkman";   //真实姓名
        String KHospital = "hospital";   //医院
        String KDepartment = "department";   //科室
        String KHospitalLevel = "hospitalLevel";  //医院等级
        String KCmeId = "cmeId";  //CME卡号
        String KLicence = "licence";   //执业许可证
        String KTitle = "title";   //职称
        String KMajor = "major";    //专长
        String KCategory = "category";    //专科一级
        String KName = "name";    //专科二级
        String KProvince = "province";   //省份
        String KCity = "city";    //城市
        String KArea = "zone";  //区

        String KMobile = "mobile";    //手机号
        String KAddress = "address";   //地址
    }

    private interface UpHeadImgParam {
        String KFile = "file";   //文件
    }

    public interface CityParam {
        String KCity = "preId";  //省份ID
    }

    public interface EpnRechargeParam {
        String KBody = "body";  //商品描述
        String KSubject = "subject";  //商品名称
        String KTotalAmount = "totalAmount";  //商品价格
    }

    private interface AttentionParam {
        String KMasterId = "masterId";    // 关注/取消关注的单位号id
        String KStatus = "status";     // 0:取消关注 1：关注
    }

    private interface UnitNumDetailParam {
        String KId = "id";  //单位号id
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

    public interface EpcParam {
        String KGoodsId = "id";  //商品id
    }

    public interface CollectMeetingParam {
        String KMeetingId = "meetId";
        String KTurnTo = "turnTo";
    }

    public interface CollectionParam {
        String KType = "type";
        String KDataFileId = "dataFileId";
        String KCollectionStatus = "resourceId";
    }

    public interface ThomsonParam {
        String KPreId = "preId";
        String KCategoryId = "categoryId";
    }

    public interface DataUnitParam {
        String KPreId = "preId";    //父级文件夹的id,第一级不用传preId
        String KType = "type";      //type=0代表汤森,type=1代表药品目录，type=2代表临床
        String KLeaf = "leaf";      //下一级是否是文件夹, 下一级为文件返回true,下一级是文件夹返回false.第一级传null或空字符串
        String KCategoryId = "categoryId";//上级文件夹id
        String KDataFileId = "dataFileId";//文件id
        String KKeyword = "keyword";    //关键字
    }

    public interface WXParam {
        String KCode = "code";
        String KOpenId = "openid";
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
                .retry(5, 1000)
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
     * 会议评论 web socket
     *
     * @return
     */
    public static NetworkReq commentIM(String meetId) {
        return NetworkReq.newBuilder(UrlMeet.KWs + UrlUtil.getBaseHost() + UrlMeet.KIm)
                .param(CommonParam.KToken, Profile.inst().getString(TProfile.token))
                .param(MeetParam.KMeetId, meetId)
                .build();
    }

    /**
     * 逐项更改用户信息
     *
     * @param key
     * @param val
     * @return
     */
    public static NetworkReq modifyProfile(String key, String val) {
        return newPost(UrlUser.KModify)
                .param(key, val)
                .build();
    }

}
