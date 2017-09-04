package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.network.model.param.CommonPair;
import lib.ys.util.DeviceUtil;
import lib.ys.util.PackageUtil;
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
        ps.add(newPair(BaseParam.KAppVersion, PackageUtil.getAppVersion()));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.KToken, Profile.inst().getString(TProfile.token)));
        }
        return ps;
    }

    private static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }

    public interface BaseParam {
        String KOSVersion = "os_version";
        String KDevice = "os_type";
        String KAppVersion = "app_version";
    }

    public interface CommonParam {
        String KToken = "token";
        String KPageNum = "pageNum";
        String KPageSize = "pageSize";
    }

    public interface MeetParam {
        String KMeetId = "meetId";//会议
        String KDetailId = "detailId";//微课明细
        String KQuestionId = "questionId";//试题
        String KAnswer = "answer";//答案
        String KItemJson = "itemJson";//答案列表
        String KUseTime = "usedtime";//微课用时  秒
        String KFinish = "finished"; //  是否完成
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
