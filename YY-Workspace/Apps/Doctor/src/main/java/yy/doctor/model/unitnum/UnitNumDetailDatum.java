package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import yy.doctor.model.unitnum.UnitNumDetailDatum.TUnitNumDetailDatum;

/**
 * Created by XPS on 2017/5/11.
 */

public class UnitNumDetailDatum extends EVal<TUnitNumDetailDatum> {

    public enum TUnitNumDetailDatum{
        id,  //资料id
        materialName,   //资料名称
        materialUrl,   //资料url
    }

}
