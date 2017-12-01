package jx.doctor.model.data;

import lib.ys.model.EVal;
import jx.doctor.model.data.DataUnitDetails.TDataUnitDetails;

/**
 * @auther WangLan
 * @since 2017/8/2
 */

public class DataUnitDetails extends EVal<TDataUnitDetails> {

    public enum TDataUnitDetails {
        @Bind(asList = DataUnitDetail.class)
        detailList,

        favorite,
    }
}
