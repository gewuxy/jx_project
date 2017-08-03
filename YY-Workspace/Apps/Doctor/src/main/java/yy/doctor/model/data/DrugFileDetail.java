package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.DrugFileDetail.TDrugFileDetail;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */

class DrugFileDetail extends EVal<TDrugFileDetail> {

    public enum TDrugFileDetail {
        id,         // 文件id
        title,      // 文件标题
        dataFrom,   // 文件来源
        author,     // 作者
        fileSize,   // 文件大小, 单位: KB
        filePath,   // 文件地址
        updateDate, // 修订日期
    }
}
