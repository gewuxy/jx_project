package yy.doctor.network.builder;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.RegisterParam;
import yy.doctor.network.UrlUtil.UrlRegister;

/**
 * 注册用的Builder
 */
public class RegisterBuilder {
    private Builder mBuilder;

    public RegisterBuilder() {
        mBuilder = NetFactory.newPost(UrlRegister.KRegister);
    }

    /**
     * @param invite 邀请码
     */
    public RegisterBuilder invite(String invite) {
        mBuilder.param(RegisterParam.KInvite, invite);
        return this;
    }

    /**
     * @param username 用户登录名
     */
    public RegisterBuilder username(String username) {
        mBuilder.param(RegisterParam.KUsername, username);
        return this;
    }

    /**
     * @param nickname 用户昵称
     */
    public RegisterBuilder nickname(String nickname) {
        mBuilder.param(RegisterParam.KNickname, nickname);
        return this;
    }

    /**
     * @param linkman 真实姓名
     */
    public RegisterBuilder linkman(String linkman) {
        mBuilder.param(RegisterParam.KLinkman, linkman);
        return this;
    }

    /**
     * @param mobile 手机号
     */
    public RegisterBuilder mobile(String mobile) {
        mBuilder.param(RegisterParam.KMobile, mobile);
        return this;
    }

    /**
     * @param pwd 密码
     */
    public RegisterBuilder pwd(String pwd) {
        mBuilder.param(RegisterParam.KPwd, pwd);
        return this;
    }

    /**
     * @param province 省份
     */
    public RegisterBuilder province(String province) {
        mBuilder.param(RegisterParam.KProvince, province);
        return this;
    }

    /**
     * @param city 城市
     */
    public RegisterBuilder city(String city) {
        mBuilder.param(RegisterParam.KCity, city);
        return this;
    }

    /**
     * @param area 区县
     * @return
     */
    public RegisterBuilder area(String area) {
        mBuilder.param(RegisterParam.KArea, area);
        return this;
    }

    /**
     * @param hospital 医院名称
     */
    public RegisterBuilder hospital(String hospital) {
        mBuilder.param(RegisterParam.KHospital, hospital);
        return this;
    }

    /**
     * @param department 科室名称
     */
    public RegisterBuilder department(String department) {
        mBuilder.param(RegisterParam.KDepartment, department);
        return this;
    }

    /**
     * @param licence 执业许可证号
     */
    public RegisterBuilder licence(String licence) {
        mBuilder.param(RegisterParam.KLicence, licence);
        return this;
    }

    public NetworkReq build() {
        return mBuilder.build();
    }
}