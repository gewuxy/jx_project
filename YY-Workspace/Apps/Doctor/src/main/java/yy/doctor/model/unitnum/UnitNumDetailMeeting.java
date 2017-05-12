package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import yy.doctor.model.unitnum.UnitNumDetailMeeting.TUnitNumDetailMeeting;

/**
 * Created by XPS on 2017/5/11.
 */

public class UnitNumDetailMeeting extends EVal<TUnitNumDetailMeeting> {

    public enum TUnitNumDetailMeeting {

        meetName,	//会议名称
        meetType,	//会议类型
        publishTime,	//发布时间
        duration,	//会议时长
        status,	//会议状态

        }

}
