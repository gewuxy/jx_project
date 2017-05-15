package yy.doctor.network.builder;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.MeetParam;
import yy.doctor.network.UrlUtil.UrlMeet;

/**
 * 签到用的Builder
 */
public class SignBuilder {
    private Builder mBuilder;

    public SignBuilder() {
        mBuilder = NetFactory.newPost(UrlMeet.KSign);
    }

    /**
     * 会议ID
     */
    public SignBuilder meetId(String meetId) {
        mBuilder.param(MeetParam.KMeetId, meetId);
        return this;
    }

    /**
     * 模块ID
     */
    public SignBuilder moduleId(String moduleId) {
        mBuilder.param(MeetParam.KModuleId, moduleId);
        return this;
    }

    /**
     * 签到位置ID
     */
    public SignBuilder positionId(String positionId) {
        mBuilder.param(MeetParam.KPositionId, positionId);
        return this;
    }

    /**
     * 签到经度
     */
    public SignBuilder signLng(String signLng) {
        mBuilder.param(MeetParam.KSignLng, signLng);
        return this;
    }

    /**
     * 签到维度
     */
    public SignBuilder signLat(String signLat) {
        mBuilder.param(MeetParam.KSignLat, signLat);
        return this;
    }

    public NetworkReq builder() {
        return mBuilder.build();
    }
}