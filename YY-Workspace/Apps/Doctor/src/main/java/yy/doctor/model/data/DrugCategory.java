package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.DrugCategory.TCategory;

/**
 * @auther Huoxuyu
 * @since 2017/8/2
 */

public class DrugCategory extends EVal<TCategory>{

    public enum TCategory {
        id,     //文件夹id
        name,   //文件夹名称
        leaf,   //下一级是否有文件夹
    }
}
