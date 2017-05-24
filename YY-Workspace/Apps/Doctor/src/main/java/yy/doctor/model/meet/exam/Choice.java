package yy.doctor.model.meet.exam;

import lib.ys.model.EVal;
import yy.doctor.model.meet.exam.Choice.TChoice;

/**
 * 考试/问卷 考题的选项
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
