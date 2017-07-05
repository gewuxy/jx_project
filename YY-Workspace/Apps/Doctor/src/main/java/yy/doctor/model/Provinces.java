package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Provinces.TProvinces;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */

public class Provinces extends EVal<TProvinces>{

    public enum TProvinces {
        alpha,
        id,
        level,
        name,
        preId,
        regionType,
        spell,
    }
}
