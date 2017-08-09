package yy.doctor.model.hospital;

import lib.ys.model.EVal;
import yy.doctor.model.hospital.HospitalLevel.THospitalLevel;

/**
 * @auther : GuoXuan
 * @since : 2017/8/9
 */

public class HospitalLevel extends EVal<THospitalLevel> {

    public enum THospitalLevel {
        id,
        picture, // 图片名字
        propValue, // 等级名字

    }
}
