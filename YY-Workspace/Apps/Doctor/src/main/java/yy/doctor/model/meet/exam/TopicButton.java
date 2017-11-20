package yy.doctor.model.meet.exam;

import lib.ys.model.EVal;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.exam.TopicButton.TTopicButton;

/**
 * @auther : GuoXuan
 * @since : 2017/11/16
 */

public class TopicButton extends EVal<TTopicButton> implements ISubject {

    @Override
    public int getType() {
        return SubjectType.button;
    }

    public enum TTopicButton {
        text,
    }

}
