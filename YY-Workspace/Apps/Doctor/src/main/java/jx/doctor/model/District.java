package jx.doctor.model;

import lib.ys.model.EVal;
import jx.doctor.model.District.TDistrict;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class District extends EVal<TDistrict> {

    public enum TDistrict {

        alpha,
        id,
        level,
        name,
        preId,
        regionType,
        spell
    }

}
