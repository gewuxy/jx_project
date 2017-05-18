package yy.doctor.model;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.param.CommonPair;
import yy.doctor.Constants.TEducationBg;
import yy.doctor.Constants.TSex;

/**
 * global config 全局配置
 * @author CaiXiang
 * @since 2017/5/17
 */

public class GlConfig {

    private List<CommonPair> mSexConfigs;
    private List<CommonPair> mEducationBgConfigs;

    public List<CommonPair> getSexConfigs() {
        return mSexConfigs;
    }

    public List<CommonPair> getmEducationBgConfigs() {
        return mEducationBgConfigs;
    }

    private GlConfig() {

        mSexConfigs = new ArrayList<>();
        TSex[] tSexes = TSex.values();
        for (TSex tSex : tSexes) {
            CommonPair pair = new CommonPair(tSex.getSex(), tSex.getId());
            mSexConfigs.add(pair);
        }

        mEducationBgConfigs = new ArrayList<>();
        TEducationBg[] tEducationBgs = TEducationBg.values();
        for (TEducationBg tEducationBg : tEducationBgs) {
            CommonPair pair = new CommonPair(tEducationBg.getmEducationBg(), tEducationBg.getmEducationBg());
            mEducationBgConfigs.add(pair);
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
