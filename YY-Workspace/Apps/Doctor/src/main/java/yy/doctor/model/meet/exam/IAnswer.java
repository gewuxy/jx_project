package yy.doctor.model.meet.exam;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther : GuoXuan
 * @since : 2017/9/12
 */

public interface IAnswer {

    @IntDef({
            AnswerType.choice,
            AnswerType.fill,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnswerType {
        int choice = 0; // 单选
        int fill = 1; // 填空
    }

    @AnswerType
    int getType();

}
