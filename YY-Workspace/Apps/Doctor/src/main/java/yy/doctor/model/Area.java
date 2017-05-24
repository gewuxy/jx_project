package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Area.TArea;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class Area extends EVal<TArea> {

    public enum TArea {

        alpha,
        id,
        level,
        name,
        preId,
        regionType,
        spell
    }

}
