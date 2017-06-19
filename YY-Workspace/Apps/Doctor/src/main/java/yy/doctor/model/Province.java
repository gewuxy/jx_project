package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Province.TProvince;

/**
 * @author CaiXiang
 * @since 2017/5/5
 */
public class Province extends EVal<TProvince> {

    public enum TProvince {
        alpha,
        id,
        name,
        preId,
        spell,
    }

}
