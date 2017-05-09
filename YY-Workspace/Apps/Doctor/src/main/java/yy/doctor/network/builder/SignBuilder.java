package yy.doctor.network.builder;

import lib.network.model.NetworkRequest;
import lib.network.model.NetworkRequest.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.MeetParam;
import yy.doctor.network.UrlUtil.UrlMeet;

/**
 * 签到用的Builder
 */
public class SignBuilder {
    private Builder mBuilder;

    public SignBuilder() {
        mBuilder = NetFactory.newGet(UrlMeet.sign);
    }

    /**
     * 会议ID
     */
    public SignBuilder meetId(String meetId) {
        mBuilder.param(MeetParam.meetId, meetId);
        return this;
    }

    /**
     * 模块ID
     */
    public SignBuilder moduleId(String moduleId) {
        mBuilder.param(MeetParam.moduleId, moduleId);
        return this;
    }

    /**
     * 签到位置ID
     */
    public SignBuilder positionId(String positionId) {
        mBuilder.param(MeetParam.positionId, positionId);
        return this;
    }

    /**
     * 签到经度
     */
    public SignBuilder signLng(String signLng) {
        mBuilder.param(signLng, signLng);
        return this;
    }

    /**
     * 签到维度
     */
    public SignBuilder signLat(String signLat) {
        mBuilder.param(signLat, signLat);
        return this;
    }

    public NetworkRequest builder() {
        return mBuilder.build();
    }
}