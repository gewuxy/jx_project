package yy.doctor.model.meet.exam;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 考试/问卷 试题信息
 *
 * @author : GuoXuan
 * @since : 2017/4/29
 */

public class Topic extends EVal<TTopic> {

    @IntDef({
            TopicType.choice_single,
            TopicType.choice_multiple,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TopicType {
        int choice_single = 0; // 单选
        int choice_multiple = 1; // 多选
    }

    public enum TTopic {
        id, // 试题ID

        /**
         * {@link TopicType}
         */
        qtype, // 试题类型

        title, // 问题
        rightKey, // 正确答案
        sort, // 试题序号

        @Bind(asList = Choice.class)
        options, // 考试 / 问卷选项

        /**
         * 本地字段
         */
        finish, // 已作答
    }
}
