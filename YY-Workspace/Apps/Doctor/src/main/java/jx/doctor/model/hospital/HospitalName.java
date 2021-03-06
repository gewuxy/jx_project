package jx.doctor.model.hospital;

import lib.ys.model.EVal;
import jx.doctor.model.hospital.HospitalName.THospitalName;

/**
 * @author CaiXiang
 * @since 2017/8/31
 */

public class HospitalName extends EVal<THospitalName> {

    public  enum THospitalName {
         name,

        @Bind( value = HospitalLevel.class)
        level,
    }
}
