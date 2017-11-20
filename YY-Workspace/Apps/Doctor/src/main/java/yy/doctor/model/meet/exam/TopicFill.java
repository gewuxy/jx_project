package yy.doctor.model.meet.exam;

import lib.ys.model.EVal;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.exam.TopicFill.TTopicFill;

/**
 * 填空
 *
 * @auther : GuoXuan
 * @since : 2017/11/16
 */

public class TopicFill extends EVal<TTopicFill> implements ISubject {

    @Override
    public int getType() {
        return SubjectType.fill;
    }

    public enum TTopicFill {
        text,
    }

}
