package yy.doctor.model.meet.topic;

import yy.doctor.model.constants.SubjectType;

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
