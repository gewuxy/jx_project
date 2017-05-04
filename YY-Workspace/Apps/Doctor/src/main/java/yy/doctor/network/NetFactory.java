package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import lib.network.param.NameValuePair;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.UrlUtil.UrlRegister;
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

    private interface RegisterParam {
        String invite = "invite";
        String username = "username";
        String nickname = "nickname";
        String linkman = "linkman";
        String mobile = "mobile";
        String pwd = "password";
        String province = "provice";
        String city = "city";
        String hospital = "hospital";
        String department = "department";
        String licence = "licence";
    }

    private interface UserParam {
        String username = "username";
        String password = "password";
    }

    /**
     * 注册
     *
     * @param invite     邀请码
     * @param username   用户登录名
     * @param nickname   用户昵称
     * @param linkman    真实姓名
     * @param mobile     手机号
     * @param pwd        密码
     * @param province   省份
     * @param city       城市
     * @param hospital   医院名称
     * @param department 科室名称
     * @param licence    执业许可证号
     * @return
     */
    public static NetworkRequest register(
            String invite,
            String username,
            String nickname,
            String linkman,
            String mobile,
            String pwd,
            String province,
            String city,
            String hospital,
            String department,
            String licence) {
        return newGet(UrlRegister.register)
                .param(RegisterParam.invite, invite)
                .param(RegisterParam.username, username)
                .param(RegisterParam.nickname, nickname)
                .param(RegisterParam.linkman, linkman)
                .param(RegisterParam.mobile, mobile)
                .param(RegisterParam.pwd, pwd)
                .param(RegisterParam.province, province)
                .param(RegisterParam.city, city)
                .param(RegisterParam.hospital, hospital)
                .param(RegisterParam.department, department)
                .param(RegisterParam.licence, licence)
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

    public static NetworkRequest logout(String token){
        return newGet("")
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
                .param(getBaseHeader());
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
                .header(getBaseHeader());
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
                .header(getBaseHeader());
    }

    private static List<NameValuePair> getBaseHeader() {
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
