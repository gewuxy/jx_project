package yy.doctor.model.exam;

import lib.ys.model.EVal;
import yy.doctor.model.exam.Answer.TAnswer;

/**
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Answer extends EVal<TAnswer>{
    public enum TAnswer {
        answer,
        id,
    }
}
