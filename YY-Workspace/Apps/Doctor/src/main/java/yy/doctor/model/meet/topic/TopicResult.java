package yy.doctor.model.meet.topic;

import lib.ys.model.EVal;
import yy.doctor.model.meet.topic.TopicResult.TTopicResult;

/**
 * @auther : GuoXuan
 * @since : 2017/6/8
 */
public class TopicResult extends EVal<TTopicResult> {

    public enum TTopicResult {
        errorCount, // 错误题数
        rightCount, // 正确题数
        score, // 得分
        totalCount, // 总题数
    }
}
