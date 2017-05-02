package yy.doctor.model.exam;

import lib.ys.model.EVal;
import yy.doctor.model.exam.ExamTopic.TExamTopic;

/**
 * @author : GuoXuan
 * @since : 2017/4/29
 */

public class ExamTopic extends EVal<TExamTopic> {

    public enum TExamTopic {
        type,//类型
        question,//问题
        answer,//答案
        finish,//已作答
    }
}
