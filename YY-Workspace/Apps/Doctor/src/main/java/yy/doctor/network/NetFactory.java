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

    public static List<CommonPair> getBaseHeader() {
        List<CommonPair> ps = new ArrayList<>();

        ps.add(newPair(CommonParam.KDevice, "android"));
        ps.add(newPair(CommonParam.KOSVersion, DeviceUtil.getSystemVersion()));
        ps.add(newPair(CommonParam.KAppVersion, PackageUtil.getAppVersionCode()));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.KToken, Profile.inst().getString(TProfile.token)));
        }
        return ps;
    }

    private static CommonPair newPair(String key, Object value) {
        return new CommonPair(key, value);
    }

    public interface CommonParam {
        String KOSVersion = "os_version";
        String KDevice = "os_type";
        String KAppVersion = "app_version";
        String KToken = "token";
    }

    public interface MeetParam {
        String KMeetId = "meetId";//会议
        String KDetailId = "detailId";//微课明细
        String KQuestionId = "questionId";//试题
        String KAnswer = "answer";//答案
        //        String KItemJson = "itemJson";//答案列表
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
     * 会议直播 web socket
     *
     * @return
     */
    public static NetworkReq webLive(String url) {
        return NetworkReq.newBuilder(url)
//                .param(CommonParam.KToken, Profile.inst().getString(TProfile.token))
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
