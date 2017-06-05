package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.ThomsonData.TThomsonData;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class ThomsonData extends EVal<TThomsonData> {

    public enum  TThomsonData {
        id, //	文件id
        title,  // 文件标题
        author, // 作者
        fileSize, // 文件大小
        filePath, // 文件地址
        updateDate, //	修订日期
        }
}
