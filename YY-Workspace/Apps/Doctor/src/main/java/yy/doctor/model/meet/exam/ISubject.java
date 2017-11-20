package yy.doctor.model.meet.exam;

import yy.doctor.model.constants.SubjectType;

/**
 * @auther : GuoXuan
 * @since : 2017/11/16
 */

public interface ISubject {

    @SubjectType
    int getType();
}
