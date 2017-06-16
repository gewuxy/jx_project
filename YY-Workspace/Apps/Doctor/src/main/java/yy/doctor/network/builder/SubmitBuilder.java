package yy.doctor.network.builder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkReq.Builder;
import lib.ys.YSLog;
import yy.doctor.model.meet.exam.Answer;
import yy.doctor.model.meet.exam.Answer.TAnswer;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetFactory.MeetParam;

/**
 * 提交的Builder
 */
public class SubmitBuilder {

    private static final String TAG = SubmitBuilder.class.getSimpleName();

    private Builder mBuilder;

    public SubmitBuilder(String url) {
        mBuilder = NetFactory.newPost(url);
    }

    /**
     * 会议ID
     */
    public SubmitBuilder meetId(String meetId) {
        mBuilder.param(MeetParam.KMeetId, meetId);
        return this;
    }

    /**
     * 模块ID
     */
    public SubmitBuilder moduleId(String moduleId) {
        mBuilder.param(MeetParam.KModuleId, moduleId);
        return this;
    }

    /********************
     * 考试
     *
     * 试卷ID
     */
    public SubmitBuilder paperId(String paperId) {
        mBuilder.param(MeetParam.KPaperId, paperId);
        return this;
    }

    /********************
     * 问卷
     *
     * 问卷调查ID
     */
    public SubmitBuilder surveyId(String surveyId) {
        mBuilder.param(MeetParam.KSurveyId, surveyId);
        return this;
    }

    /********************
     * 问卷/考试
     *
     * 答案明细列表
     */
    public SubmitBuilder items(List<Answer> items) {
        JSONArray arr = new JSONArray();

        Observable.fromIterable(items)
                .subscribe(answer -> {
                    JSONObject o = new JSONObject();
                    try {
                        o.put(MeetParam.KAnswer, answer.getString(TAnswer.answer));
                        o.put(MeetParam.KQuestionId, answer.getString(TAnswer.id));
                    } catch (JSONException e) {
                        YSLog.e(TAG, MeetParam.KItemJson, e);
                    }

                    arr.put(o);
                });

        mBuilder.param(MeetParam.KItemJson, arr.toString());
        return this;
    }

    /********************
     * 微课
     */
    /**
     * 微课ID
     */
    public SubmitBuilder courseId(String courseId) {
        mBuilder.param(MeetParam.KCourseId, courseId);
        return this;
    }

    /**
     * 微课明细ID
     */
    public SubmitBuilder detailId(String detailId) {
        mBuilder.param(MeetParam.KDetailId, detailId);
        return this;
    }

    /**
     * 微课明细用时
     */
    public SubmitBuilder useTime(String useTime) {
        mBuilder.param(MeetParam.KUseTime, useTime);
        return this;
    }

    /**
     * 是否完成
     * @param finish
     * @return
     */
    public SubmitBuilder isFinish(boolean finish) {
        mBuilder.param(MeetParam.KFinish, String.valueOf(finish));
        return this;
    }

    public NetworkReq builder() {
        return mBuilder.build();
    }
}