package yy.doctor.model.hospital;

import lib.ys.model.EVal;
import yy.doctor.model.hospital.HospitalTitle.TText;

/**
 * @auther WangLan
 * @since 2017/7/19
 */

public class HospitalTitle extends EVal<TText> implements IHospital {

    @Override
    public int getType() {
        return HospitalType.hospital_title;
    }

    public enum TText {
        name,
    }
}
