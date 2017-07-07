package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class ThomsonDetail extends EVal<TThomsonDetail> {

    public enum TThomsonDetail {
        id, //	文件id
        title,  // 文件标题
        authed, //
        categoryId, //
        dataFrom, //
        author, // 作者
        fileSize, // 文件大小, 单位: KB
        filePath, // 文件地址
        rootCategory, //
        summary, //
        updateDate, //	修订日期
    }
}
