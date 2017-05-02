package yy.doctor.model.exam;

import yy.doctor.model.BaseGroup;


/**
 * @author : GuoXuan
 * @since : 2017/4/29
 */

public class GroupExamTopic extends BaseGroup<ExamTopic> {
    private String mQuestion;

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        mQuestion = question;
    }
}
