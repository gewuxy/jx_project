package jx.doctor.model.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 试题
 *
 * @author : GuoXuan
 * @since : 2017/11/16
 */
@IntDef({
        SubjectType.title,
        SubjectType.choose,
        SubjectType.fill,
        SubjectType.button,
})
@Retention(RetentionPolicy.SOURCE)
public @interface SubjectType {
    int title = 0; // 题目
    int choose = 1; // 选项
    int fill = 2; // 填空
    int button = 3; // 按钮
}