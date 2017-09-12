package yy.doctor.model.meet.exam;

import lib.ys.model.EVal;
import yy.doctor.model.meet.exam.Fill.TFill;

/**
 * @auther : GuoXuan
 * @since : 2017/9/12
 */

public class Fill extends EVal<TFill> implements IAnswer {

    @Override
    public int getType() {
        return AnswerType.fill;
    }

    public enum TFill {

        /**
         * 本地字段
         */
        answer
    }
}
