package jx.doctor.model.meet.topic;

import jx.doctor.model.constants.SubjectType;

/**
 * 考题的模块类型
 *
 * @auther : GuoXuan
 * @since : 2017/11/16
 */

public interface ITopic {

    @SubjectType
    int getType();
}
