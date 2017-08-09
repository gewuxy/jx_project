package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.unitnum.UnitNumDetail.TUnitNumDetail;

/**
 * Created by CaiXiang on 2017/5/11.
 */

public class UnitNumDetail extends EVal<TUnitNumDetail> {

    public static final int KFileLimit = 3; // 资料最多显示多少条

    public enum TUnitNumDetail {
        attention,  //是否已经关注单位号 1 已经关注  0 未关注
        id,      //单位号id
        nickname,    //昵称
        headimg,    //头像
        attentionNum,    //关注人数
        province,    //省份
        city,    //城市
        sign,    //简介
        materialNum,  //资料总数量

        //单位号资料
        @Bind(asList = FileData.class)
        materialList,

        //会议
        @Bind(asList = Meeting.class)
        meetFolderList,
    }

}
