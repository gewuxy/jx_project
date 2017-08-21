package yy.doctor;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public interface Constants extends BaseConstants {

    String KAppId = "wx83d3ea20a714b660";

    @StringDef({
            MeetStateText.under_way,
            MeetStateText.not_started,
            MeetStateText.retrospect,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface MeetStateText {
        String under_way = "进行中";
        String not_started = "未开始";
        String retrospect = "精彩回顾";
    }

    interface PageConstants {
        int KPage = 1;  // 起始页页数
        int KPageSize = 20;  //分页加载时 每页加载的条目数
    }

    @IntDef({
            DateUnit.hour,
            DateUnit.minute,
            DateUnit.second,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface DateUnit {
        int hour = 0;
        int minute = 1;
        int second = 2;
    }

    @IntDef({
            CaptchaType.fetch,
            CaptchaType.re_fetch,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface CaptchaType {
        int fetch = 0;
        int re_fetch = 1;
    }

    @StringDef({
            WXType.login,
            WXType.bind,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface WXType {
        String login = "login";
        String bind = "bind";
    }

    /**
     * 性别
     */
    enum TSex {
        man(1, "男"),
        female(2, "女");

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

        public String getEducationBg() {
            return mEducationBg;
        }
    }

    /**
     * 医院级别
     */
    enum THospitalGrade {
        one_level("一级"),
        two_level("二级"),
        three_level("三级"),
        community_service_center("社区卫生服务中心"),
        health_centers("卫生院"),
        clinic("诊所");
        //others("其他");

        private String mHospitalGrade;

        THospitalGrade(String mHospitalGrade) {
            this.mHospitalGrade = mHospitalGrade;
        }

        public String getHospitalGrade() {
            return mHospitalGrade;
        }
    }

    /**
     * 医生职称 高级 中级 初级 其他
     */
    enum TDoctorGrade {
        high("高级"),
        middle("中级"),
        primary("初级"),
        other("其他");

        private String mDoctorGrade;

        TDoctorGrade(String mDoctorGrade) {
            this.mDoctorGrade = mDoctorGrade;
        }

        public String getDoctorGrade() {
            return mDoctorGrade;
        }
    }

    /**
     * 医生职称  医师 药师 护师 技师 其他
     */
    enum TDoctorCategory {
        physician("医师"),
        apothecary("药师"),
        nurse("护师"),
        technician("技师"),
        other("其他");

        private String mDoctorCategory;

        TDoctorCategory(String mDoctorCategory) {
            this.mDoctorCategory = mDoctorCategory;
        }

        public String getDoctorCategory() {
            return mDoctorCategory;
        }
    }
}
