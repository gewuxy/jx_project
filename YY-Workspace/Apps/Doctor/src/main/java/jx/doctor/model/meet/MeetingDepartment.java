package jx.doctor.model.meet;

import lib.ys.model.EVal;
import jx.doctor.model.meet.MeetingDepartment.TMeetingDepartment;

/**
 * @auther WangLan
 * @since 2017/8/2
 */

public class MeetingDepartment extends EVal<TMeetingDepartment> {

    public enum TMeetingDepartment {
        count,
        icon,
        name,
    }
}
