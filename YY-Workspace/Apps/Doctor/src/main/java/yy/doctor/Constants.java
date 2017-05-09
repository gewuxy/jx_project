package yy.doctor;

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

    interface MeetsState {
        int KWait = 1;//未开始
        int KPro = 2;//进行中
        int KReview = 3;//已结束
    }

}
