package yy.doctor.model.meet.exam;

import lib.ys.model.EVal;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.exam.TopicChoice.TTopicChoice;

/**
 * 考试/问卷 考题的选项
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */
public class TopicChoice extends EVal<TTopicChoice> implements ISubject {

    @Override
    public int getType() {
        return SubjectType.choose;
    }

    public enum TTopicChoice {
        key, // 选项
        value, // 选项内容

        /**
         * 本地字段
         */
        check, // 选择情况
    }
}
