package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.network.model.param.CommonPair;
import lib.ys.util.DeviceUtil;
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

        ps.add(newPair(BaseParam.KDevice, "android"));
        ps.add(newPair(BaseParam.KOSVersion, DeviceUtil.getSystemVersion()));
        ps.add(newPair(BaseParam.KAppVersion, DeviceUtil.getAppVersion()));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.KToken, Profile.inst().getString(TProfile.token)));
        }
        return ps;
    }

    private static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }

    private interface BaseParam {
        String KOSVersion = "os_version";
        String KDevice = "os_type";
        String KAppVersion = "app_version";
    }

    private interface CommonParam {
        String KToken = "token";
        String KPreId = "preId";
        String KOffset = "offset";
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
        String KHosLevel = "hosLevel";//医院级别
        String KDepartment = "department";//科室
        String KInvite = "invite";//科室
        String KTitle = "title";//邀请码
        String KLicence = "licence";//执业许可证号
        String KCaptcha = "captcha";//注册验证码
        String KMasterId = "masterId";//二维码参数，激活码提供方id
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
        String KPageNum = "pageNum";//第N次分页
        String KPageSize = "pageSize";//返回多少条数据

        String KSignLng = "signLng";//经度
        String KSignLat = "signLat";//维度

        String KFinish = "finished"; //  是否完成
        String KInfinityId = "infinityId";
    }

    private interface HomeParam {
        String KType = "type";
        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
    }

    public interface ProfileParam {
        String KHeadImgUrl = "headimg";   //头像地址
        String KLinkman = "linkman";   //真实姓名
        String KHospital = "hospital";   //医院
        String KDepartment = "department";   //科室
        String KHospitalLevel = "hosLevel";  //医院等级
        String KCmeId = "cmeId";  //CME卡号
        String KLicence = "licence";   //执业许可证
        String KTitle = "title";   //职称
        String KMajor = "major";    //专长
        String KCategory = "category";    //专科一级
        String KName = "name";    //专科二级
        // FIXME 测试
        String KSpecialty_name = "specialty_name"; //专科
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

    public interface EpcParam {
        String KGoodsId = "id";  //商品id
        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
    }

    public interface CollectMeetingParam {
        String KMeetingId = "meetId";
        String KTurnTo = "turnTo";
    }

    public interface CollectionParam {
        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
        String KType = "type";
        String KDataFileId = "dataFileId";
        String KCollectionStatus = "resourceId";
    }

    public interface ThomsonParam {
        String KPreId = "preId";
        String KCategoryId = "categoryId";
        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
    }

    public interface DrugParam {
        String KPreId = "preId";    //父级文件夹的id,第一级不用传preId
        String KType = "type";      //type=0代表汤森,type=1代表药品目录，type=2代表临床
        String KLeaf = "true";      //下一级是否是文件夹, 下一级为文件返回true,下一级是文件夹返回false.第一级传null或空字符串
        String KCategoryId = "categoryId";//上级文件夹id
        String KPageNum = "pageNum";    //当前页数
        String KPageSize = "pageSize";  //显示条数
        String KDataFileId = "dataFileId";//文件id
        String KKeyWord = "keyWord";    //关键字
    }

    public interface WXParam {
        String KCode = "code";
        String KOpenId = "openid";
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
     * 验证码
     *
     * @param mobile 手机号
     * @param type   type=0或者空时表示注册时获取验证码,1表示重置密码时获取验证码
     * @return
     */
    public static NetworkReq captcha(String mobile, int type) {
        return newGet(UrlRegister.KCaptcha)
                .param(RegisterParam.KMobile, mobile)
                .param(RegisterParam.KType, type)
                .build();
    }

    /**
     * 二维码，有masterId
     *
     * @param masterId
     * @return
     */
    public static NetworkReq scan(String masterId) {
        return newGet(UrlRegister.KScan)
                .param(RegisterParam.KMasterId, masterId)
                .build();
    }

    /**
     * 二维码，没有masterId
     *
     * @return
     */
    public static NetworkReq scan() {
        return newGet(UrlRegister.KScan)
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
     * 专科信息
     *
     * @return
     */
    public static NetworkReq specialty() {
        return newGet(UrlRegister.KSpecialty)
                .build();
    }

    /**
     * 职称信息
     *
     * @return
     */
    public static NetworkReq title() {
        return newGet(UrlRegister.KTitle)
                .build();
    }


    /**
     * 登录(绑定微信号)
     *
     * @param name
     * @param pwd
     * @return
     */
    public static NetworkReq login(String name, String pwd, String openId) {
        return newPost(UrlUser.KLogin)
                .param(UserParam.KUserName, name)
                .param(UserParam.KPassword, pwd)
                .param(WXParam.KOpenId, openId)
                .build();
    }

    /**
     * 检查是否已被绑定
     *
     * @param code
     * @return
     */
    public static NetworkReq check_wx_bind(String code) {
        return newPost(UrlUser.KBindWX)
                .param(WXParam.KCode, code)
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
                .retry(5, 1000)
                .build();
    }

    /**
     * 通过邮箱找回密码
     *
     * @param username
     * @return
     */
    public static NetworkReq forgetPwd(String username) {
        return newGet(UrlUser.KForgetPwdEmail)
                .param(UserParam.KUserName, username)
                .build();
    }

    /**
     * 通过手机找回密码
     *
     * @param mobile
     * @param captcha
     * @param passWord
     * @return
     */
    public static NetworkReq forgetPwd(String mobile, String captcha, String passWord) {
        return newGet(UrlUser.KForgetPwdPhone)
                .param(RegisterParam.KMobile, mobile)
                .param(RegisterParam.KCaptcha, captcha)
                .param(RegisterParam.KPassword, passWord)
                .build();
    }

    /**
     * 登出
     *
     * @return
     */
    public static NetworkReq logout(String token) {
        return newGet(UrlUser.KLogout)
                .param(CommonParam.KToken, token)
                .retry(5, 1000)
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
    public static NetworkReq recommendMeeting(int page, int pageSize) {
        return newGet(UrlHome.KRecommendMeeting)
                .param(HomeParam.KPageNum, page)
                .param(HomeParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 首页推荐会议文件夹
     *
     * @return
     */
    public static NetworkReq recommendFolder() {
        return newGet(UrlHome.KRecommendMeetingFolder)
                .build();
    }

    /**
     * 文件夹
     *
     * @return
     */
    public static NetworkReq meetFolder(String preId) {
        return newGet(UrlMeet.KMeetingFolder)
                .param(CommonParam.KPreId, preId)
                .build();
    }

    /**
     * 文件夹里的文件夹
     *
     * @return
     */
    public static NetworkReq folderResource(String infinityId) {
        return newGet(UrlMeet.KMeetingFolderResource)
                .param(MeetParam.KInfinityId, infinityId)
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
    public static NetworkReq upHeadImg(byte[] bytes) {
        return newUpload(UrlUser.KUpHeaderImg)
                .param(UpHeadImgParam.KFile, bytes)
                .build();
    }

    /**
     * 修改密码
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
     * 获取省市区的资料
     *
     * @param id
     * @return
     */
    public static NetworkReq pcd(String... id) {
        if (id != null && id.length > 0) {
            return newGet(UrlRegister.KCity)
                    .param(CityParam.KCity, id[0])
                    .build();
        } else {
            return newGet(UrlRegister.KProvince).build();
        }
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
     * 检查app版本
     *
     * @return
     */
    public static NetworkReq checkAppVersion() {
        return newGet(UrlUser.KCheckAppVersion)
                .build();
    }

    /**
     * 收藏的会议列表
     *
     * @param pageNum
     * @param pageSize
     * @return 该值为空或0时，表示会议类型
     */
    public static NetworkReq collection(int pageNum, int pageSize, int type) {
        return newGet(UrlUser.KCollection)
                .param(CollectionParam.KPageNum, pageNum)
                .param(CollectionParam.KPageSize, pageSize)
                .param(CollectionParam.KType, type)
                .build();
    }

    /**
     * 收藏的药品目录详情
     * @param dataFileId
     * @return
     */
    public static NetworkReq drugDetail(String dataFileId){
        return newGet(UrlUser.KDrugDetail)
                .param(CollectionParam.KDataFileId,dataFileId)
                .build();
    }

    public static NetworkReq collectionStatus(String resourceId,String type){
        return newGet(UrlUser.KCollectionStatus)
                .param(CollectionParam.KCollectionStatus,resourceId)
                .param(CollectionParam.KType,type)
                .build();
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
     * @param page
     * @param pageSize
     * @return
     */
    public static NetworkReq order(int page, int pageSize) {
        return newGet(UrlEpc.KOrder)
                .param(EpcParam.KPageNum, page)
                .param(EpcParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 象城
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static NetworkReq epc(int page, int pageSize) {
        return newGet(UrlEpc.KEpc)
                .param(EpcParam.KPageNum, page)
                .param(EpcParam.KPageSize, pageSize)
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
                .param(EpcParam.KGoodsId, id)
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
    public static NetworkReq unitNumData(String id, int pageNum, int pageSize) {
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
    public static NetworkReq thomsonAll(int page, int pageSize) {
        return newGet(UrlData.KThomsonAll)
                .param(ThomsonParam.KPageNum, page)
                .param(ThomsonParam.KPageSize, pageSize)
                .build();
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
     * 药品目录(文件或文件夹列表)
     *
     * @param preId 父级文件夹的id,第一级不用传preId
     * @param type  type=0,null代表汤森，type=1代表药品目录，type=2代表临床
     * @return
     */
    public static NetworkReq drugCategory(String preId, int type, boolean leaf, int pageNum, int pageSize) {
        return newGet(UrlData.KDrugCategory)
                .param(DrugParam.KPreId, preId)
                .param(DrugParam.KType, type)
                .param(DrugParam.KLeaf, String.valueOf(leaf))
                .param(DrugParam.KPageNum, pageNum)
                .param(DrugParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 药品目录文件列表
     *
     * @param categoryId    上级文件夹id
     * @param pageNum       当前页数
     * @param pageSize      显示条数
     * @return
     */
    public static NetworkReq drugAllFile(String categoryId, int pageNum, int pageSize){
        return newGet(UrlData.KDrugAllFile)
                .param(DrugParam.KCategoryId, categoryId)
                .param(DrugParam.KPageNum, pageNum)
                .param(DrugParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 药品目录文章内容
     *
     * @param dataFileId    文件id
     * @return
     */
    public static NetworkReq drugDataDetail(String dataFileId){
        return newGet(UrlData.KDrugDataDetail)
                .param(DrugParam.KDataFileId, dataFileId)
                .build();
    }

    /**
     * 搜索药品或临床指南
     *
     * @param keyWord   关键字
     * @param type      type=1代表药品目录，type=2代表临床
     * @param pageNum   当前页数
     * @param pageSize  显示条数
     * @return
     */
    public static NetworkReq drugSearch(String keyWord, int type, int pageNum, int pageSize){
        return newGet(UrlData.KDrugSearch)
                .param(DrugParam.KKeyWord, keyWord)
                .param(DrugParam.KType, type)
                .param(DrugParam.KPageNum, pageNum)
                .param(DrugParam.KPageSize, pageSize)
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
    public static NetworkReq searchUnitNum(String keyword, int page, int pageSize) {
        return newPost(UrlSearch.KSearchUnitNum)
                .param(SearchParam.KKeyword, keyword)
                .param(MeetParam.KPageNum, page)
                .param(MeetParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 搜索会议
     *
     * @param keyword
     * @return
     */
    public static NetworkReq searchMeeting(String keyword, int page, int pageSize) {
        return newPost(UrlSearch.KSearchMeeting)
                .param(SearchParam.KKeyword, keyword)
                .param(MeetParam.KPageNum, page)
                .param(MeetParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 关注过的公众的所有会议
     */
    public static NetworkReq meets(int state, String depart, int page, int pageSize) {
        return newPost(UrlMeet.KMeets)
                .param(MeetParam.KState, state)
                .param(MeetParam.KDepart, depart)
                .param(MeetParam.KPageNum, page)
                .param(MeetParam.KPageSize, pageSize)
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

    /**
     * 会议资料
     *
     * @param meetingId
     * @return
     */
    public static NetworkReq meetingData(String meetingId, int pageNum, int pageSize) {
        return newGet(UrlMeet.KMeetingData)
                .param(MeetParam.KMeetId, meetingId)
                .param(UnitNumDetailParam.KPageNum, pageNum)
                .param(UnitNumDetailParam.KPageSize, pageSize)
                .build();
    }

    /**
     * 会议科室列表
     * @return
     */
    public static NetworkReq meetingDepartment(){
        return newPost(UrlMeet.KMeetingDepartment)
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
        return newGet(UrlMeet.KToVideo)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KModuleId, moduleId)
                .build();
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
        return new SubmitBuilder(UrlMeet.KSubmitPPT);
    }

    /**
     * 视频学习提交
     */
    public static SubmitBuilder submitVideo() {
        return new SubmitBuilder(UrlMeet.KMeetingVideoRecord);
    }


    /**
     * 会议时间提交
     */
    public static NetworkReq submitMeet(String meetId, long useTime) {
        return newGet(UrlMeet.KMeetingRecord)
                .param(MeetParam.KMeetId, meetId)
                .param(MeetParam.KUseTime, useTime)
                .retry(6, 1000)
                .build();
    }

    /**
     * 签到
     */
    public static SignBuilder sign() {
        return new SignBuilder();
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
    public static NetworkReq histories(String meetId, int pageSize, int pageNum) {
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
     * 绑定手机号
     *
     * @param mobile
     * @param captcha
     * @return
     */
    public static NetworkReq bindMobile(String mobile, String captcha) {
        return newGet(UrlUser.KBindMobile)
                .param(RegisterParam.KMobile, mobile)
                .param(RegisterParam.KCaptcha, captcha)
                .build();
    }

    /**
     * 绑定邮箱
     *
     * @param email
     * @return
     */
    public static NetworkReq bindEmail(String email) {
        return newGet(UrlUser.KBindEmail)
                .param(RegisterParam.KUsername, email)
                .build();
    }

    /**
     * 解绑邮箱
     *
     * @return
     */
    public static NetworkReq unBindEmail() {
        return newGet(UrlUser.KUnBindEmail)
                .build();
    }

    /**
     * 绑定微信
     *
     * @param code
     * @return
     */
    public static NetworkReq bindWX(String code) {
        return newGet(UrlUser.KBindWXSet)
                .param(WXParam.KCode, code)
                .build();
    }


    /**
     * 参会统计(个人参会统计)
     *
     * @return
     */
    public static NetworkReq statsMeet(int offset) {
        return newPost(UrlMeet.KStatsAttend)
                .param(CommonParam.KOffset,offset)
                .build();
    }

    /**
     * 参会统计(关注单位号发布会议统计)
     *
     * @return
     */
    public static NetworkReq statsUnitNum(int offset) {
        return newPost(UrlMeet.KStatsPublish)
                .param(CommonParam.KOffset,offset)
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
