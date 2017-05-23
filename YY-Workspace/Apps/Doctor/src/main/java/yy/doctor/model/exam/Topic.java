package yy.doctor.model.exam;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.exam.Topic.TTopic;

/**
 * 考试/问卷试题信息
 *
 * @author : GuoXuan
 * @since : 2017/4/29
 */

public class Topic extends EVal<TTopic> {

    public enum TTopic {
        id,//试题ID
        qtype,//试题类型
        title,//问题
        rightKey,//正确答案
        sort,//问卷序号
        @BindList(Choice.class)
        options,//考试选项
        @BindList(Choice.class)
        optionList,//问卷选项
        /**
         * 本地字段
         */
        finish,//已作答
        answer,//答案
    }
}
