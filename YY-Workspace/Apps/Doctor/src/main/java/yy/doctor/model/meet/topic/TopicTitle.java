package yy.doctor.model.meet.topic;

import lib.ys.model.EVal;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.topic.TopicTitle.TTopicTitle;

/**
 * 考题的题目
 *
 * @auther : GuoXuan
 * @since : 2017/11/16
 */
public class TopicTitle extends EVal<TTopicTitle> implements ITopic {

    @Override
    public int getType() {
        return SubjectType.title;
    }

    public enum TTopicTitle {
        name,
    }

}
