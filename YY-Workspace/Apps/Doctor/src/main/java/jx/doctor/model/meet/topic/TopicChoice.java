package jx.doctor.model.meet.topic;

import lib.ys.model.EVal;
import jx.doctor.model.constants.SubjectType;
import jx.doctor.model.meet.topic.TopicChoice.TTopicChoice;

/**
 * 考试/问卷 考题的选项
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */
public class TopicChoice extends EVal<TTopicChoice> implements ITopic {

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
