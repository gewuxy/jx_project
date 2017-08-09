package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.DataDetail.TDrugDetailData;

/**
 * @auther WangLan
 * @since 2017/8/2
 */

public class DataDetail extends EVal<TDrugDetailData> {

    public enum TDrugDetailData {
        @Bind(asList = DrugDetail.class)
        detailList,

        favorite,
    }
}
