package yy.doctor.model.meet.topic;

import lib.ys.model.EVal;
import yy.doctor.model.meet.topic.TopicPaper.TTopicPaper;

/**
 * 考试/问卷 试题大概信息(只包含试卷信息)
 * 含{@link Topic}
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */
public class TopicPaper extends EVal<TTopicPaper> {

    public enum TTopicPaper {
        id, // 试卷id
        name, // 试卷名称
        paperName, // 问卷名称

        @Bind(asList = Topic.class)
        questions, // 试卷包含的试题列表
    }
}
