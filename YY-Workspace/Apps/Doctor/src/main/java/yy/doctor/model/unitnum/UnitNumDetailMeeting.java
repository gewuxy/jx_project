package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import yy.doctor.model.unitnum.UnitNumDetailMeeting.TUnitNumDetailMeeting;

/**
 * Created by XPS on 2017/5/11.
 */

public class UnitNumDetailMeeting extends EVal<TUnitNumDetailMeeting> {

    public enum TUnitNumDetailMeeting {

        id,        //会议id
        meetName,    //会议名称
        meetType,    //会议类型  科室
        startTime,    //开始时间
        endTime,    //结束时间
        state,    //会议状态  0表示草稿 1表示未开始 2表示进行中 3已结束 4已关闭'
    }

}
