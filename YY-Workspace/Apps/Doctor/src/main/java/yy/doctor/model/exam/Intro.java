package yy.doctor.model.exam;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindObj;
import yy.doctor.model.exam.Intro.TIntro;

/**
 * 考试/问卷信息
 *
 * @author : GuoXuan
 * @since : 2017/5/5
 */

public class Intro extends EVal<TIntro> {
    public enum TIntro {
        id,//试卷id
        meetId,//会议ID
        moduleId,//模块ID
        serverTime,//服务器当前时间
        startTime,//起始时间
        endTime,//结束时间
        usetime,//考试用时
        @BindObj(Paper.class)
        paper,//试卷(问卷)信息
        paperId,//会议问卷ID
    }

    private static Intro mIntro = null;

    public static Intro getIntro() {
        return mIntro;
    }

    public static void setIntro(Intro intro) {
        Intro.mIntro = intro;
    }
}
