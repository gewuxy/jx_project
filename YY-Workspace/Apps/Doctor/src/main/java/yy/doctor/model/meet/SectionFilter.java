package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.SectionFilter.TSectionFilter;

/**
 * @auther WangLan
 * @since 2017/7/28
 */

public class SectionFilter extends EVal<TSectionFilter> {
    public enum TSectionFilter{
        bitmap,
        name,
        number,
    }
}
