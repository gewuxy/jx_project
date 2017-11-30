package yy.doctor.model.meet.topic;

import lib.ys.model.EVal;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.topic.TopicFill.TTopicFill;

/**
 * 填空
 *
 * @auther : GuoXuan
 * @since : 2017/11/16
 */
public class TopicFill extends EVal<TTopicFill> implements ITopic {

    @Override
    public int getType() {
        return SubjectType.fill;
    }

    public enum TTopicFill {
        text,
    }

}
