package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.DrugCategoryData.TCategoryData;

/**
 * @auther Huoxuyu
 * @since 2017/8/3
 */

public class DrugCategoryData extends EVal<TCategoryData>{

    public enum TCategoryData{
        @Bind(asList = DrugCategory.class)
        isFolder,
        @Bind(asList = DrugFileDetail.class)
        isFile,


    }
}
