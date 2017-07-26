package yy.doctor.model;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindArray;
import yy.doctor.model.Title.TTitle;

/**
 * @auther WangLan
 * @since 2017/7/25
 */

public class Title extends EVal<TTitle> {
    public enum TTitle{
        @BindArray
        grade,

        @BindArray
        title,
    }
}
