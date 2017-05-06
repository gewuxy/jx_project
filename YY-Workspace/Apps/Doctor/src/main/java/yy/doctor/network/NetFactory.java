package yy.doctor.network;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import lib.network.param.NameValuePair;
import yy.doctor.model.Profile;
import yy.doctor.network.UrlUtil.UrlEpc;
import yy.doctor.network.UrlUtil.UrlEpn;
import yy.doctor.network.UrlUtil.UrlRegister;
import yy.doctor.network.UrlUtil.UrlUnitNum;
import yy.doctor.network.UrlUtil.UrlUser;

import static yy.doctor.model.Profile.TProfile.token;

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

    private interface ProfilePara {
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

    private interface CityPara {
        String city = "preid";
    }

    private interface EpnRechargePara {
        String body = "body";
        String subject = "subject";
        String totalAmount = "totalAmount";
    }

    private interface EpcExchangePara {
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
                .param(invite, invite)
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

    public static RegisterBuilder newRegBuilder() {
        return new RegisterBuilder();
    }

    public static class RegisterBuilder {
        private Builder mBuilder;

        private RegisterBuilder() {
            mBuilder = newGet(UrlRegister.register);
        }

        public RegisterBuilder invite(String invite) {
            mBuilder.param(invite, invite);
            return this;
        }

        public RegisterBuilder username(String username) {
            mBuilder.param(username, username);
            return this;
        }

        public NetworkRequest build() {
            return mBuilder.build();
        }
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
                .param(CommonParam.token, Profile.inst().getString(token))
                .build();
    }

    /**
     * 个人信息
     *
     * @return
     */
    public static NetworkRequest profile() {
        return newGet(UrlUser.profile)
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

    public static class ModifyBuilder {

        private Builder mBuilder;

        private ModifyBuilder() {
            mBuilder = newPost(UrlUser.modify);
        }

        /**
         * 用户昵称
         *
         * @param nickname
         * @return
         */
        public ModifyBuilder nickname(String nickname) {
            mBuilder.param(ProfilePara.nickname, nickname);
            return this;
        }

        /**
         * 真实姓名
         *
         * @param linkman
         * @return
         */
        public ModifyBuilder linkman(String linkman) {
            mBuilder.param(ProfilePara.linkman, linkman);
            return this;
        }

        /**
         * 手机号
         *
         * @param mobile
         * @return
         */
        public ModifyBuilder mobile(String mobile) {
            mBuilder.param(ProfilePara.mobile, mobile);
            return this;
        }

        /**
         * 头像地址
         *
         * @param headimg
         * @return
         */
        public ModifyBuilder headimg(String headimg) {
            mBuilder.param(ProfilePara.headimg, headimg);
            return this;
        }

        /**
         * 省份
         *
         * @param province
         * @return
         */
        public ModifyBuilder province(String province) {
            mBuilder.param(ProfilePara.province, province);
            return this;
        }

        /**
         * 城市
         *
         * @param city
         * @return
         */
        public ModifyBuilder city(String city) {
            mBuilder.param(ProfilePara.city, city);
            return this;
        }

        /**
         * 执业许可证
         *
         * @param licence
         * @return
         */
        public ModifyBuilder licence(String licence) {
            mBuilder.param(ProfilePara.licence, licence);
            return this;
        }

        /**
         * 专长
         *
         * @param major
         * @return
         */
        public ModifyBuilder major(String major) {
            mBuilder.param(ProfilePara.major, major);
            return this;
        }

        /**
         * 职务
         *
         * @param place
         * @return
         */
        public ModifyBuilder place(String place) {
            mBuilder.param(ProfilePara.place, place);
            return this;
        }

        /**
         * 职位
         *
         * @param title
         * @return
         */
        public ModifyBuilder title(String title) {
            mBuilder.param(ProfilePara.title, title);
            return this;
        }

        /**
         * 医院
         *
         * @param hospital
         * @return
         */
        public ModifyBuilder hospital(String hospital) {
            mBuilder.param(ProfilePara.hospital, hospital);
            return this;
        }

        /**
         * 科室
         *
         * @param department
         * @return
         */
        public ModifyBuilder department(String department) {
            mBuilder.param(ProfilePara.department, department);
            return this;
        }

        /**
         * 地址
         *
         * @param address
         * @return
         */
        public ModifyBuilder address(String address) {
            mBuilder.param(ProfilePara.address, address);
            return this;
        }

        public NetworkRequest builder() {
            return mBuilder.build();
        }

    }

    /**
     * 省份
     *
     * @return
     */
    public static NetworkRequest province() {
        return newGet(UrlRegister.province)
                .build();
    }

    /**
     * 城市
     *
     * @return
     */
    public static NetworkRequest city(String preid) {
        return newGet(UrlRegister.city)
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
     * @return
     */
    public static ExchangeBuilder newExchangeBuilder() {
        return new ExchangeBuilder();
    }

    public static class ExchangeBuilder {

        private Builder mBuilder;

        private ExchangeBuilder() {
            mBuilder = newPost(UrlEpc.exchange);
        }

        /**
         * 商品id
         * @param goodsId
         * @return
         */
        public ExchangeBuilder goodsId(String goodsId) {
            mBuilder.param(EpcExchangePara.goodsId, goodsId);
            return this;
        }

        /**
         * 价格
         * @param price
         * @return
         */
        public ExchangeBuilder price(String price) {
            mBuilder.param(EpcExchangePara.price, price);
            return this;
        }

        /**
         *收货人
         * @param receiver
         * @return
         */
        public ExchangeBuilder receiver(String receiver) {
            mBuilder.param(EpcExchangePara.receiver, receiver);
            return this;
        }

        /**
         * 手机号码
         * @param phone
         * @return
         */
        public ExchangeBuilder phone(String phone) {
            mBuilder.param(EpcExchangePara.phone, phone);
            return this;
        }

        /**
         *省份
         * @param province
         * @return
         */
        public ExchangeBuilder province(String province) {
            mBuilder.param(EpcExchangePara.province, province);
            return this;
        }

        /**
         * 地址
         * @param address
         * @return
         */
        public ExchangeBuilder address(String address) {
            mBuilder.param(EpcExchangePara.address, address);
            return this;
        }

        /**
         * 限购数量
         * @param buyLimit
         * @return
         */
        public ExchangeBuilder buyLimit(String buyLimit) {
            mBuilder.param(EpcExchangePara.buyLimit, buyLimit);
            return this;
        }

        public NetworkRequest builder() {
            return mBuilder.build();
        }

    }


    /**
     * 订单
     * @return
     */
    public static NetworkRequest order() {
        return newGet(UrlEpc.order)
                .build();
    }

    /**
     * 象城
     * @return
     */
    public static NetworkRequest epc() {
        return newGet(UrlEpc.epc)
                .build();
    }

    /**
     * 关注的单位号
     * @return
     */
    public static NetworkRequest unitNum() {
        return newGet(UrlUnitNum.unitNum)
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
                .header(getBaseHeader());
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
            ps.add(newPair(CommonParam.token, Profile.inst().getString(token)));
        }

        return ps;
    }

    private static NameValuePair newPair(String key, Object value) {
        return new NameValuePair(key, value);
    }

}
