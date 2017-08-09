package yy.doctor.model.hospital;

import lib.ys.model.EVal;
import yy.doctor.model.hospital.HospitalLevelInfo.THospitalLevelInfo;

/**
 * @auther : GuoXuan
 * @since : 2017/8/9
 */

public class HospitalLevelInfo extends EVal<THospitalLevelInfo>{

    public enum THospitalLevelInfo{

        version, // 版本号

        @Bind(asList = HospitalLevel.class)
        propList,

    }
}
