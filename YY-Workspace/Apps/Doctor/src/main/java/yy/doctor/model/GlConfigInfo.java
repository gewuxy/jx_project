package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.GlConfigInfo.TGlConfigInfo;
import yy.doctor.model.hospital.HospitalLevel;

/**
 * 配置信息
 *
 * @auther : GuoXuan
 * @since : 2017/8/9
 */

public class GlConfigInfo extends EVal<TGlConfigInfo> {

    public enum TGlConfigInfo {

        version, // 版本号

        @Bind(asList = HospitalLevel.class)
        propList,

    }
}
