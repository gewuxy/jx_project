package jx.csp.network;

import jx.csp.constant.Constants;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.sp.SpApp;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.network.model.pair.Pairs;
import lib.ys.util.DeviceUtil;
import lib.ys.util.PackageUtil;
import lib.ys.util.TextUtil;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class NetFactory {

    /**
     * 获取post请求
     *
     * @param url url
     * @return Builder
     */
    public static Builder newPost(String url) {
        return NetworkReq.newBuilder(UrlUtil.getBaseUrl() + url)
                .post()
                .header(getBaseHeader());
    }

    /**
     * 会议信息更改
     *
     * @param courseId courseId
     * @param title    title
     * @param imgId    imgId
     * @param musicId  musicId
     * @return NetworkReq
     */
    public static NetworkReq update(String courseId,
                                    String title,
                                    int imgId,
                                    int musicId) {
        Builder builder = NetworkReq.newBuilder(UrlUtil.getBaseUrl() + "/meeting/mini/update")
                .post()
                .header(getBaseHeader());
        builder.param("courseId", courseId);
        if (TextUtil.isNotEmpty(title)) {
            builder.param("courseId", courseId);
        }
        if (imgId != Constants.KInvalidValue) {
            builder.param("imgId", imgId);
        }
        if (musicId != Constants.KInvalidValue) {
            builder.param("musicId", musicId);
        }
        return builder.build();
    }

    public interface CommonParam {
        String KOSVersion = "os_version";
        String KDevice = "os_type";
        String KAppVersion = "app_version";
        String KToken = "token";
        String KLocal = "_local"; //所有接口请求的header中传递 _local 字段作为国际化标识、
        String KAbroad = "abroad"; //接口中， 如果是海外版需在header中传递 abroad=1 标识
    }

    public static Pairs getBaseHeader() {
        Pairs ps = new Pairs();

        ps.add(CommonParam.KDevice, "android");
        ps.add(CommonParam.KOSVersion, DeviceUtil.getSystemVersion());
        ps.add(CommonParam.KAppVersion, PackageUtil.getAppVersionCode());
        ps.add(CommonParam.KLocal, SpApp.inst().getLangType().define());
        ps.add(CommonParam.KAbroad, SpApp.inst().getAppType());

        if (Profile.inst().isLogin()) {
            ps.add(CommonParam.KToken, Profile.inst().getString(TProfile.token));
        }
        return ps;
    }
}
