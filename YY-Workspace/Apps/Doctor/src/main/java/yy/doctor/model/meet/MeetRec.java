package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.MeetRec.TMeetRec;

/**
 * 会议推荐的数据
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class MeetRec extends EVal<TMeetRec> {
    public enum TMeetRec {
        id,             //会议ID
        lecturer,       //主讲者
        lecturerTile,   //主讲者职位
        meetName,       //会议名称
        meetType,       //会议科室类型
        organizer,      //会议主办方
        state,          //会议状态
    }
}
