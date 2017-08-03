package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.ThomsonDetailData.TThomsonDetailData;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class ThomsonDetailData extends EVal<TThomsonDetailData> {

    public enum TThomsonDetailData {
        @Bind(asList = ThomsonDetail.class)
        detailList,

        favorite,
    }
}
