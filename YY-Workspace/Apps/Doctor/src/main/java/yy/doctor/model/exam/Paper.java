package yy.doctor.model.exam;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.Profile;
import yy.doctor.model.exam.Paper.TPaper;
import yy.doctor.sp.SpUser;

/**
 * 考试/问卷具体信息
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Paper extends EVal<TPaper> {
    public enum TPaper {
        id,//试卷id
        name,//试卷名称
        paperName,//问卷名称
        @BindList(Topic.class)
        questions,//试卷包含的试题列表
        serverTime,//服务器当前时间
        usetime,//考试时间
    }

}
