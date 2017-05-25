package yy.doctor.model.config;

import java.util.ArrayList;
import java.util.List;

import yy.doctor.Constants.TDoctorCategory;
import yy.doctor.Constants.TDoctorGrade;
import yy.doctor.Constants.TEducationBg;
import yy.doctor.Constants.THospitalGrade;
import yy.doctor.Constants.TSex;

/**
 * global config 全局配置
 * @author CaiXiang
 * @since 2017/5/17
 */

public class GlConfig {

    private List<Config> mSexConfigs;
    private List<Config> mEducationBgConfigs;
    private List<Config> mHospitalGrade;
    private List<GroupConfig> mTitle;

    public List<Config> getSexConfigs() {
        return mSexConfigs;
    }

    public List<Config> getEducationBgConfigs() {
        return mEducationBgConfigs;
    }

    public List<Config> getHospitalGrade() {
        return mHospitalGrade;
    }

    public List<GroupConfig> getTitle() {
        return mTitle;
    }

    private GlConfig() {

        mSexConfigs = new ArrayList<>();
        TSex[] tSexes = TSex.values();
        for (TSex tSex : tSexes) {
            Config pair = new Config(tSex.getSex(), tSex.getId());
            mSexConfigs.add(pair);
        }

        mEducationBgConfigs = new ArrayList<>();
        TEducationBg[] tEducationBgs = TEducationBg.values();
        for (TEducationBg tEducationBg : tEducationBgs) {
            Config pair = new Config(tEducationBg.getEducationBg(), tEducationBg.getEducationBg());
            mEducationBgConfigs.add(pair);
        }

        mHospitalGrade = new ArrayList<>();
        THospitalGrade[] tHospitalGrades = THospitalGrade.values();
        for (THospitalGrade tHospitalGrade : tHospitalGrades) {
            Config pair = new Config(tHospitalGrade.getHospitalGrade(), tHospitalGrade.getHospitalGrade());
            mHospitalGrade.add(pair);
        }

        mTitle = new ArrayList<>();
        TDoctorGrade[] tDoctorGrades = TDoctorGrade.values();
        TDoctorCategory[] tDoctorCategories = TDoctorCategory.values();
        List<Config> doctorCategories = new ArrayList<>();
        for (TDoctorCategory tDoctorCategory : tDoctorCategories) {
            Config config = new Config(tDoctorCategory.getDoctorCategory(), tDoctorCategory.getDoctorCategory());
            doctorCategories.add(config);
        }
        for (TDoctorGrade tDoctorGrade : tDoctorGrades) {
            Config config = new Config(tDoctorGrade.getDoctorGrade(), tDoctorGrade.getDoctorGrade());
            GroupConfig groupConfig = new GroupConfig(config, doctorCategories);
            mTitle.add(groupConfig);
        }

    }

    private static GlConfig mInst = null;
    public synchronized static GlConfig inst() {
        if (mInst == null) {
            if (mInst == null) {
                mInst = new GlConfig();
            }
        }
        return mInst;
    }

}
