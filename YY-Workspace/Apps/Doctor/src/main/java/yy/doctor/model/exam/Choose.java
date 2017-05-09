package yy.doctor.model.exam;

import lib.ys.model.EVal;
import yy.doctor.model.exam.Choose.TExamChoose;

/**
 * 选项
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

class Choose extends EVal<TExamChoose> {
    public enum TExamChoose {
        key,//选项
        value,//选项内容
    }
}
