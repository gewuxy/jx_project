package jx.doctor.model.meet.topic;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import jx.doctor.model.meet.topic.Topic.TTopic;

/**
 * 考试/问卷 试题具体信息(一套题目)
 *
 * @author : GuoXuan
 * @since : 2017/4/29
 */

public class Topic extends EVal<TTopic> {

    public CharSequence getType() {
        switch (getInt(TTopic.qtype)) {
            case TopicType.choice_single: {
                return "单选";
            }
            case TopicType.choice_multiple: {
                return "多选";
            }
            case TopicType.fill: {
                return "填空";
            }
        }
        return "单选";
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

        @Bind(asList = TopicChoice.class)
        options, // 考试 / 问卷选项

        /**
         * 本地字段
         */
        finish, // 已作答

        choice, // 作答答案

        subject, // 本地展示的数据
    }

    @IntDef({
            TopicType.choice_single,
            TopicType.choice_multiple,
            TopicType.fill,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TopicType {
        int choice_single = 0; // 单选
        int choice_multiple = 1; // 多选
        int fill = 2; // 填空
    }
}
