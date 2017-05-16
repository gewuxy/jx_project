package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.unitnum.UnitNumDetail.TUnitNumDetail;

/**
 * Created by XPS on 2017/5/11.
 */

public class UnitNumDetail extends EVal<TUnitNumDetail> {

    public enum TUnitNumDetail {

        nickname,    //昵称
        headimg,    //头像
        attentionNum,    //关注人数
        province,    //省份
        city,    //城市
        sign,    //简介
        materialNum,  //资料总数量

        //单位号资料
        @BindList(UnitNumDetailDatum.class)
        materialDTOList,

        //会议
        @BindList(UnitNumDetailMeeting.class)
        meetingDTOList,

    }

}
