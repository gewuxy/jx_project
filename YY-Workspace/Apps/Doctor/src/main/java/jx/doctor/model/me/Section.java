package jx.doctor.model.me;

import lib.ys.model.EVal;
import jx.doctor.model.me.Section.TSection;

/**
 * 科室的数据
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Section extends EVal<TSection> {
    public enum TSection {
        category,//隶属
        id,
        name,//名字
    }
}