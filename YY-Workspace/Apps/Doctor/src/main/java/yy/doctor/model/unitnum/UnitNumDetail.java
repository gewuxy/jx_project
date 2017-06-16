package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.unitnum.UnitNumDetail.TUnitNumDetail;

/**
 * Created by XPS on 2017/5/11.
 */

public class UnitNumDetail extends EVal<TUnitNumDetail> {

    public enum TUnitNumDetail {

        attention,  //是否已经关注单位号 1 已经关注  0 未关注
        id,      //公众号id
        nickname,    //昵称
        headimg,    //头像
        attentionNum,    //关注人数
        province,    //省份
        city,    //城市
        sign,    //简介
        materialNum,  //资料总数量

        //单位号资料
        @BindList(FileData.class)
        materialDTOList,

        //会议
        @BindList(Meeting.class)
        meetingDTOList,
    }

}
