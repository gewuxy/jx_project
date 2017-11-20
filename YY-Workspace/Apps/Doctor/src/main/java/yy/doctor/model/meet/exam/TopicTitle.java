package yy.doctor.model.meet.exam;

import lib.ys.model.EVal;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.exam.TopicTitle.TTopicTitle;

/**
 * @auther : GuoXuan
 * @since : 2017/11/16
 */

public class TopicTitle extends EVal<TTopicTitle> implements ISubject {

    @Override
    public int getType() {
        return SubjectType.title;
    }

    public enum TTopicTitle {
        name,
    }

}
