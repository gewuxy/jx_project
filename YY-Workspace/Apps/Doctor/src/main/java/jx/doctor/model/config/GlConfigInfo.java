package jx.doctor.model.config;

import lib.ys.model.EVal;
import jx.doctor.model.config.GlConfigInfo.TGlConfigInfo;
import jx.doctor.model.hospital.HospitalLevel;

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
