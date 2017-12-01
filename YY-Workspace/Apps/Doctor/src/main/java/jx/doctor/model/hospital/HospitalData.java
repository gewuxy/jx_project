package jx.doctor.model.hospital;

import lib.ys.model.EVal;
import jx.doctor.model.hospital.HospitalData.THospitalData;

/**
 * @auther WangLan
 * @since 2017/7/19
 */

public class HospitalData extends EVal<THospitalData> implements IHospital {
    @Override
    public int getType() {
        return HospitalType.hospital_data;
    }

    public enum THospitalData {
        name,
        distance,
        address,
    }
}
