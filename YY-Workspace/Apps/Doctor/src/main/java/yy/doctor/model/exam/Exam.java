package yy.doctor.model.exam;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindObj;
import yy.doctor.model.exam.Exam.TExam;

/**
 * 考试/问卷信息
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Exam extends EVal<TExam> {
    public enum TExam {
        meetId,//会议ID
        moduleId,//模块ID
        @BindObj(Paper.class)
        paper,//试卷(问卷)信息
        paperId,//会议问卷ID
    }
}
