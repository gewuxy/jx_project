package jx.doctor.model.data;

import lib.ys.model.EVal;
import jx.doctor.model.data.DrugDetail.TDrugDetail;


/**
 * @auther WangLan
 * @since 2017/8/1
 */

public class DrugDetail extends EVal<TDrugDetail> {

    public enum TDrugDetail {
        detailKey, // 文章的标题
        detailValue, // 文章的内容
        id,
        dataFileId, // 文件id

        favorite,   //是否收藏,ture表示已收藏,false表示未收藏
    }
}
