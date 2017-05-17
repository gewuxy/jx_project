package yy.doctor.model.exam;

import lib.ys.model.EVal;
import yy.doctor.model.exam.Choose.TChoose;

/**
 * 选项
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Choose extends EVal<TChoose> {
    public enum TChoose {
        key,//选项
        value,//选项内容
    }
}
