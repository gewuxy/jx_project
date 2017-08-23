package yy.doctor.model.config;

import java.util.List;

import yy.doctor.model.hospital.HospitalLevel;

/**
 * @auther yuansui
 * @since 2017/8/23
 */

public class GlConfig {
    private static GlConfig mInst;

    private List<HospitalLevel> mLevels;

    private GlConfig() {
    }

    public synchronized static GlConfig inst() {
        if (mInst == null) {
            mInst = new GlConfig();
        }
        return mInst;
    }

    public void setHospitalLevels(List<HospitalLevel> levels) {
        mLevels = levels;
    }

    public List<HospitalLevel> getHospitalLevels() {
        return mLevels;
    }

}
