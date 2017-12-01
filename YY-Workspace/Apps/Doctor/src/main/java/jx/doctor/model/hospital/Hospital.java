package jx.doctor.model.hospital;

import lib.ys.model.EVal;
import jx.doctor.model.hospital.Hospital.THospital;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class Hospital extends EVal<THospital> implements IHospital {

    public int getType() {
        return HospitalType.hospital_data;
    }

    public enum THospital {
        alpha,//首字母
        name,//名字
        distance,
        address,
    }
}
