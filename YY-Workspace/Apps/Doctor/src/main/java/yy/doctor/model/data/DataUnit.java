package yy.doctor.model.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.data.DataUnit.TDataUnit;

/**
 * 文件和文件夹属性合并
 *
 * @auther Huoxuyu
 * @since 2017/8/2
 */

public class DataUnit extends EVal<TDataUnit> {

    /**
     * 文件打开方式
     * Integer类型，1代表用pdf打开，2代表请求文件详情接口打开，3代表用html打开
     */
    @IntDef({
            FileOpenType.pdf,
            FileOpenType.details,
            FileOpenType.html,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FileOpenType {
        int pdf = 1; // 调用pdf
        int details = 2; // 调用接口
        int html = 3; // 调用webview
    }

    public enum TDataUnit {
        /**
         * 共用
         */
        id,     //文件或文件夹id
        title,      // 文件或文件夹的标题
        isFile, // 是否是文件夹

        /**
         * 文件
         */
        dataFrom,   // 文件来源
        author,     // 作者
        fileSize,   // 文件大小, 单位: KB
        filePath,   // 文件地址
        updateDate, // 修订日期

        /**
         * html
         */
        htmlPath,

        /**
         * 文件夹
         */
        leaf,   //下一级是否有文件夹

        /**
         * {@link FileOpenType}
         */
        openType,
    }
}
