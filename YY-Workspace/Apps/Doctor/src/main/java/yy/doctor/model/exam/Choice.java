package yy.doctor.model.exam;

import lib.ys.model.EVal;
import yy.doctor.model.exam.Choice.TChoice;

/**
 * 选项
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Choice extends EVal<TChoice> {
    public enum TChoice {
        key,//选项
        value,//选项内容

        check, //选择情况
    }
}
