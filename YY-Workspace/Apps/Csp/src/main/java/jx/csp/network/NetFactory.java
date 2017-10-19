package jx.csp.network;

import java.util.ArrayList;
import java.util.List;

import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.UrlUtil.UrlUser;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.network.model.param.CommonPair;
import lib.ys.util.DeviceUtil;
import lib.ys.util.PackageUtil;

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

//        ps.add(newPair(CommonParam.KToken, Profile.inst().getString(TProfile.token)));
//        ps.add(newPair(CommonParam.KToken, "99efb894ecac41ccab822811ffc79276"));
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

    public interface LoginParam {
        String KUseName = "username";
        String KPwd = "password";
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

    public static NetworkReq yayaAuthorize(String username, String password) {
        return NetworkReq.newBuilder(UrlUtil.getYaYaLogin())
                .post()
                .header(getBaseHeader())
                .param(LoginParam.KUseName, username)
                .param(LoginParam.KPwd, password)
                .build();
    }

}
