package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.City.TCity;

/**
 * @author CaiXiang
 * @since 2017/5/5
 */
public class City extends EVal<TCity> {

    public enum TCity {
        alpha,
        id,
        level,
        name,
        preId,
        regionType,
        spell,
    }

}
