package yy.doctor.model.meet.exam;

import java.io.Serializable;

import lib.ys.model.EVal;
import yy.doctor.model.meet.exam.Answer.TAnswer;

/**
 * 考试/问卷 选择的答案
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Answer extends EVal<TAnswer> implements Serializable {

    public enum TAnswer {
        id,//题号
        answer,//答案
    }
}
