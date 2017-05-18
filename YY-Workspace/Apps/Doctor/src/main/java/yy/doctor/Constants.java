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


    /**
     * 性别
     */
    enum TSex {
        man(1,"男"),
        female(2,"女");

        private int mId;
        private String mSex;
        TSex(int id, String sex) {
            mId = id;
            mSex = sex;
        }
        public int getId() {
           return mId;
        }
        public String getSex() {
            return mSex;
        }
    }

    /**
     * 学历
     */
    enum TEducationBg {
        junior_college("专科"),
        regular_college("本科"),
        master("硕士"),
        doctor("博士"),
        postdoctor("博士后");

        private String mId;
        private String mEducationBg;
        TEducationBg(String mEducationBg) {
            this.mEducationBg = mEducationBg;
        }
        public String getmEducationBg() {
            return mEducationBg;
        }
    }

}
