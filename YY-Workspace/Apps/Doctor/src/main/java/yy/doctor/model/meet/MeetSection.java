package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.MeetSection.TMeetSection;

/**
 * @author : GuoXuan
 * @since : 2017/5/12
 */

public class MeetSection extends EVal<TMeetSection> {
    public enum TMeetSection {
        count,//会议数量
        name,//科室类型名称
    }
}
