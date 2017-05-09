package yy.doctor.model.exam;

import lib.ys.model.EVal;
import yy.doctor.model.exam.Answer.TAnswer;

/**
 * 选择的答案
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Answer extends EVal<TAnswer>{
    public enum TAnswer {
        answer,//答案
        id,//题号
    }
}
