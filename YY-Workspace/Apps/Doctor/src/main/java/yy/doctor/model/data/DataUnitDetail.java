package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.DataUnitDetail.TDataUnitDetail;


/**
 * @auther WangLan
 * @since 2017/8/1
 */

public class DataUnitDetail extends EVal<TDataUnitDetail> {

    public enum TDataUnitDetail {
        detailKey, // 文章的标题
        detailValue, // 文章的内容
        id,
        dataFileId, // 文件id
    }
}
