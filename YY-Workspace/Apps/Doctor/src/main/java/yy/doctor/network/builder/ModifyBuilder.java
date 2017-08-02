package yy.doctor.network.builder;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.ProfileParam;
import yy.doctor.network.UrlUtil.UrlUser;

/**
 * 修改个人资料的Builder
 */
public class ModifyBuilder {

    private Builder mBuilder;

    public ModifyBuilder() {
        mBuilder = NetFactory.newPost(UrlUser.KModify);
    }

    /**
     * 真实姓名
     *
     * @param linkman
     * @return
     */
    public ModifyBuilder linkman(String linkman) {
        mBuilder.param(ProfileParam.KLinkman, linkman);
        return this;
    }


    /**
     * 手机号
     *
     * @param mobile
     * @return
     */
    public ModifyBuilder mobile(String mobile) {
        mBuilder.param(ProfileParam.KMobile, mobile);
        return this;
    }

    /**
     * 头像地址
     *
     * @param headImgUrl
     * @return
     */
    public ModifyBuilder headImgUrl(String headImgUrl) {
        mBuilder.param(ProfileParam.KHeadImgUrl, headImgUrl);
        return this;
    }

    /**
     * 医院
     *
     * @param hospital
     * @return
     */
    public ModifyBuilder hospital(String hospital) {
        mBuilder.param(ProfileParam.KHospital, hospital);
        return this;
    }

    /**
     * 医院等级
     *
     * @param hospitalLevel
     * @return
     */
    public ModifyBuilder hospitalLevel(String hospitalLevel) {
        mBuilder.param(ProfileParam.KHospitalLevel, hospitalLevel);
        return this;
    }

    /**
     * 科室
     *
     * @param department
     * @return
     */
    public ModifyBuilder department(String department) {
        mBuilder.param(ProfileParam.KDepartment, department);
        return this;
    }

    /**
     * 省份
     *
     * @param province
     * @return
     */
    public ModifyBuilder province(String province) {
        mBuilder.param(ProfileParam.KProvince, province);
        return this;
    }

    /**
     * 城市
     *
     * @param city
     * @return
     */
    public ModifyBuilder city(String city) {
        mBuilder.param(ProfileParam.KCity, city);
        return this;
    }

    /**
     * 区县
     *
     * @param area
     * @return
     */
    public ModifyBuilder area(String area) {
        mBuilder.param(ProfileParam.KArea, area);
        return this;
    }

    /**
     * 专科一级
     *
     * @param category
     * @return
     */
    public ModifyBuilder category(String category) {
        mBuilder.param(ProfileParam.KCategory, category);
        return this;
    }

    /**
     * 专科二级
     *
     * @param name
     * @return
     */
    public ModifyBuilder name(String name) {
        mBuilder.param(ProfileParam.KName, name);
        return this;
    }

    /**
     * CME卡号
     *
     * @param cmeId
     * @return
     */
    public ModifyBuilder cmeId(String cmeId) {
        mBuilder.param(ProfileParam.KCmeId, cmeId);
        return this;
    }

    /**
     * 执业许可证
     *
     * @param licence
     * @return
     */
    public ModifyBuilder licence(String licence) {
        mBuilder.param(ProfileParam.KLicence, licence);
        return this;
    }

    /**
     * 职称
     *
     * @param title
     * @return
     */
    public ModifyBuilder title(String title) {
        mBuilder.param(ProfileParam.KTitle, title);
        return this;
    }


    /**
     * 专长
     *
     * @param major
     * @return
     */
    public ModifyBuilder major(String major) {
        mBuilder.param(ProfileParam.KMajor, major);
        return this;
    }

    /**
     * 地址
     *
     * @param address
     * @return
     */
    public ModifyBuilder address(String address) {
        mBuilder.param(ProfileParam.KAddress, address);
        return this;
    }

    public NetworkReq build() {
        return mBuilder.build();
    }


}