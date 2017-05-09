package yy.doctor.network.builder;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.ProfilePara;
import yy.doctor.network.UrlUtil.UrlUser;

public class ModifyBuilder {

    private Builder mBuilder;

    public ModifyBuilder() {
        mBuilder = NetFactory.newPost(UrlUser.modify);
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