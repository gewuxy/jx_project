package yy.doctor.model.meet.exam;

import java.io.Serializable;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindObj;
import yy.doctor.model.meet.exam.Intro.TIntro;

/**
 * 考试/问卷 全部信息
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Intro extends EVal<TIntro> implements Serializable {
    public enum TIntro {
        id,
        meetId, // 会议ID
        moduleId, // 模块ID
        serverTime, // 服务器当前时间
        startTime, // 起始时间
        endTime, // 结束时间
        usetime, // 考试可用时间
        paperId, // 试卷/会议问卷ID
        finished, // 是否考过
        score, // 分数
        finishTimes, // 考过次数
        resitTimes, // 可考次数
        passScore, // 及格分数

        @BindObj(Paper.class)
        paper, // 试卷(问卷)信息

        /**
         * 本地字段
         */
        time, // 考试剩余时间
    }
}
