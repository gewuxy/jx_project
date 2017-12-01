package jx.doctor.model.meet.topic;

import lib.ys.model.EVal;
import jx.doctor.model.constants.SubjectType;
import jx.doctor.model.meet.topic.TopicButton.TTopicButton;

/**
 * 考题的按钮
 *
 * @auther : GuoXuan
 * @since : 2017/11/16
 */

public class TopicButton extends EVal<TTopicButton> implements ITopic {

    @Override
    public int getType() {
        return SubjectType.button;
    }

    public enum TTopicButton {
        text,
    }

}
