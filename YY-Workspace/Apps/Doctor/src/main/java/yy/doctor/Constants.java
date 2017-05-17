package yy.doctor;

import android.support.annotation.IntDef;

import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public interface Constants extends BaseConstants {

    interface SectionConstants {
        int KRowCount = 3; // 列数
        int KDividerHeight = 14; // 分割线高度
    }

    /**
     * 会议状态
     */
    @IntDef({
            MeetsState.not_started,
            MeetsState.under_way,
            MeetsState.retrospect,
    })
    @interface MeetsState {
        int not_started = 1;//未开始
        int under_way = 2;//进行中
        int retrospect = 3;//精彩回顾
    }

    interface ModuleId {
        String KPpt = "";//微课
        String KVideo = "";//视频
        String KExam = "8";//考试
        String Kue = "10";//问卷
        String KSign = "4";//签到

    }

}
