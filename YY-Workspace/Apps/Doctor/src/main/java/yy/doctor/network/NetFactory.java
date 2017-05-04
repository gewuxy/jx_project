package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import lib.network.param.NameValuePair;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.UrlUtil.UrlUser;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class NetFactory {

    private interface BaseParam {
        String device_os = "device_os";
    }

    private interface CommonParam {
        String token = "token";
    }

    private interface UserParam {
        String username = "username";
        String password = "password";
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


    /*********************************
     * 以下是工具
     */

    /**
     * 获取post请求
     *
     * @param url
     * @return
     */
    private static Builder newPost(String url) {
        return new Builder(UrlUtil.getBaseUrl() + url)
                .post()
                .param(getBaseParams());
    }

    /**
     * 获取get请求
     *
     * @param url
     * @return
     */
    private static Builder newGet(String url) {
        return new Builder(UrlUtil.getBaseUrl() + url)
                .get()
                .param(getBaseParams());
    }

    /**
     * 获取upload请求
     *
     * @param url
     * @return
     */
    private static Builder newUpload(String url) {
        return new Builder(UrlUtil.getBaseUrl() + url)
                .upload()
                .param(getBaseParams());
    }

    private static List<NameValuePair> getBaseParams() {
        List<NameValuePair> ps = new ArrayList<>();

        // TODO: ???公共参数
//        ps.add(newPair(BaseParam.device_os, "android"));

        if (Profile.inst().isLogin()) {
            ps.add(newPair(CommonParam.token, Profile.inst().getString(TProfile.token)));
        }

        return ps;
    }

    private static NameValuePair newPair(String key, Object value) {
        return new NameValuePair(key, value);
    }
}
